import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.gdax.GDAXExchange;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by julia on 7/3/2017.
 */
public class ArbMonitor {
    private static final Logger Logger = LoggerFactory.getLogger(ArbMonitor.class);

    private List<String> TargetExchangeNames;
    private HashMap<String, List<FeeSchedule>> FeeSchedules;
    private HashMap<String, MarketDataService> ExchangeServices;
    private HashMap<String, Exchange> Exchanges;
    private HashMap<String, List<CurrencyPair>> AvailablePairs;

    public ArbMonitor() throws URISyntaxException,
            IOException {

        this.TargetExchangeNames = new ArrayList<>();
        this.TargetExchangeNames.add("bitstamp");
        this.TargetExchangeNames.add("bitfinex");
        this.TargetExchangeNames.add("bittrex");
        this.TargetExchangeNames.add("btce");
        this.TargetExchangeNames.add("gdax");
        this.TargetExchangeNames.add("kraken");
        this.TargetExchangeNames.add("poloinex");
//        this.TargetExchangeNames.add("quadrigacx");

        this.Exchanges = new HashMap<>();
        this.Exchanges.put("bitstamp", ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName()));
        this.Exchanges.put("kraken", ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName()));
        this.Exchanges.put("gdax", ExchangeFactory.INSTANCE.createExchange(GDAXExchange.class.getName()));
        this.Exchanges.put("poloinex", ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName()));
        this.Exchanges.put("bitfinex", ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName()));
//        this.Exchanges.put("quadrigacx", ExchangeFactory.INSTANCE.createExchange(QuadrigaCxExchange.class.getName()));
        this.Exchanges.put("btce", ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName()));
        this.Exchanges.put("bittrex", ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()));

        this.ExchangeServices = new HashMap<>();
        this.AvailablePairs = new HashMap<>();
        for (Map.Entry<String, Exchange> entry : this.Exchanges.entrySet()) {
            this.ExchangeServices.put(entry.getKey(), entry.getValue().getMarketDataService());
            this.AvailablePairs.put(entry.getKey(), entry.getValue().getExchangeSymbols());
        }

        this.FeeSchedules = this.RetrieveExchangeInformation(this.TargetExchangeNames);
    }

    private HashMap<String, List<FeeSchedule>> RetrieveExchangeInformation(List<String> exchangeList) throws
            FileNotFoundException,
            URISyntaxException {
        HashMap<String, List<FeeSchedule>> exchangeInfo = new HashMap<>();

        for (String exchange : exchangeList) {
            URL resource = ArbMonitor.class.getResource(exchange + "_fees.csv");
            String path = null;
            path = Paths.get(resource.toURI()).toFile().getPath();
            //noinspection unchecked
            List<FeeSchedule> beans = new CsvToBeanBuilder(new FileReader(path)).withType(FeeSchedule
                    .class).build().parse();
            exchangeInfo.put(exchange, beans);
        }
        return exchangeInfo;
    }

    /**
     * Gets the latest quotes from each of the exchanges for relevant pairs The entries may be less than the number of
     * total specific pairs specified in the CSV files if the data is not available.
     *
     * @return A HashMap with key: exchange and value: a Quote object
     */
    private List<Quote> GetQuotesFromExchange(String exchangeName) throws PairNotSupportedException, IOException {
        List<Quote> quotes = new ArrayList<>();
        MarketDataService targetMds = this.ExchangeServices.get(exchangeName);
        List<FeeSchedule> targetSchedules = this.FeeSchedules.get(exchangeName);
        List<CurrencyPair> availablePairs = this.AvailablePairs.get(exchangeName);

        for (FeeSchedule schedule :
                targetSchedules) {
            String baseCcy = schedule.getBaseCurrency();
            String quoteCcy = schedule.getQuoteCurrency();
            CurrencyPair pair = new CurrencyPair(baseCcy, quoteCcy);
            Quote quote;
            Ticker tick;
            if (availablePairs.contains(pair)) {
                tick = targetMds.getTicker(pair);
                quote = new Quote(tick.getBid(), tick.getAsk(), exchangeName, pair);
            } else if (availablePairs.contains(new CurrencyPair(quoteCcy, baseCcy))) {
                // Try to flip the pair
                CurrencyPair reversePair = new CurrencyPair(quoteCcy, baseCcy);
                tick = targetMds.getTicker(reversePair);
                quote = new Quote(tick.getBid(), tick.getAsk(), exchangeName, reversePair);
            } else {
                throw new PairNotSupportedException("The pair: " + baseCcy + "/" + quoteCcy + " or its reverse is not" +
                        " " +
                        "supported" +
                        " in the API for the exchange: " + exchangeName + ".Please remove it from the file. ");
            }
            quotes.add(quote);
        }
        return quotes;
    }

    void Monitor() throws InterruptedException, TargetFileNotInitialized, IOException, CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException {

        Writer writer = new FileWriter("opportunities.csv", true);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();

        while (true) {
            List<ArbitrageOpportunity> opportunitiesToWrite = new ArrayList<>();
            ArrayList<Quote> quotes = new ArrayList<>();
            // Get quotes from all exchanges
            for (String exchange :
                    this.TargetExchangeNames) {
                try {
                    quotes.addAll(this.GetQuotesFromExchange(exchange));
                } catch (Exception e) {
                    // Swallow any IO or disconnection errors and log.
                    Logger.warn(e.getMessage());
                }
            }

            HashMap<CurrencyPair, List<Quote>> bucketedQuotes = new HashMap<>();

            // Bucket quotes into pairs to avoid unnecessary comparisons
            for (Quote quote : quotes) {
                CurrencyPair reversePair = new CurrencyPair(quote.getPair().counter, quote.getPair().base);
                List<Quote> bucket;
                if (bucketedQuotes.containsKey(quote.getPair())) {
                    bucket = bucketedQuotes.get(quote.getPair());
                    bucket.add(quote);
                } else if (bucketedQuotes.containsKey(reversePair)) {
                    bucket = bucketedQuotes.get(reversePair);
                    bucket.add(quote);
                } else {
                    // No bucket exists yet.
                    ArrayList<Quote> newBucket = new ArrayList<>();
                    newBucket.add(quote);
                    bucketedQuotes.put(quote.getPair(), newBucket);
                }
            }

            // Check each basket
            for (Map.Entry<CurrencyPair, List<Quote>> bucket : bucketedQuotes.entrySet()) {
                CurrencyPair pair = bucket.getKey();
                List<Quote> quoteBucket = bucket.getValue();
                if (quoteBucket.size() > 1) {
                    for (int i = 0; i < quoteBucket.size(); i++) {
                        for (int j = i + 1; j < quoteBucket.size(); j++) {
                            Quote firstQuote = quoteBucket.get(i);
                            Quote secondQuote = quoteBucket.get(j);
                            if (secondQuote.getBid().compareTo(firstQuote.getAsk()) > 0) {
                                opportunitiesToWrite.add(new ArbitrageOpportunity(firstQuote, secondQuote));
                                Logger.info("BUY: " + firstQuote.getPair().toString() + " on exchange " + firstQuote
                                        .getExchange() + " at: " + firstQuote.getAsk().toString() + ". \nSELL: " +
                                        secondQuote.getPair().toString() + " on exchange " + secondQuote.getExchange
                                        () + " at: " + secondQuote.getBid().toString());
                            } else if (firstQuote.getBid().compareTo(secondQuote.getAsk()) > 0) {
                                opportunitiesToWrite.add(new ArbitrageOpportunity(secondQuote, firstQuote));
                                Logger.info("BUY: " + secondQuote.getPair().toString() + " on exchange " +
                                        secondQuote.getExchange() + " at: " + secondQuote.getAsk().toString() + ". " +
                                        "\nSELL: " +
                                        firstQuote.getPair().toString() + " on exchange " + firstQuote.getExchange()
                                        + " at: "
                                        + firstQuote.getBid().toString());
                            }
                        }
                    }
                }
            }
            // Write opportunities to csv
            try {
                //noinspection unchecked
                beanToCsv.write(opportunitiesToWrite);
                writer.flush();
            } catch (IOException e) {
                Logger.warn("Unable to read file 'opportunities.csv'. Skipping writing this round.", e);
            }

            Logger.info("Refreshing ...");
            Thread.sleep(5000);
        }
    }
}
