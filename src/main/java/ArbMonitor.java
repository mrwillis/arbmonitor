import com.opencsv.bean.CsvToBeanBuilder;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
            Quote quote = null;
            Ticker tick;
            if (availablePairs.contains(pair)) {
                tick = targetMds.getTicker(pair);
                quote = new Quote(baseCcy, quoteCcy, tick.getBid(), tick.getAsk());
            } else if (availablePairs.contains(new CurrencyPair(quoteCcy, baseCcy))) {
                // Try to flip the pair
                CurrencyPair reversePair = new CurrencyPair(quoteCcy, baseCcy);
                tick = targetMds.getTicker(reversePair);
                quote = new Quote(quoteCcy, baseCcy, tick.getBid(), tick.getAsk());
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

    void Monitor() throws InterruptedException {

        while (true) {
            HashMap<String, List<Quote>> quotes = new HashMap<>();
            for (String exchange :
                    this.TargetExchangeNames) {
                try {
                    quotes.put(exchange, this.GetQuotesFromExchange(exchange));
                } catch (PairNotSupportedException pairNotSupportedException) {
                    Logger.warn(pairNotSupportedException.getMessage());
                } catch (Exception e) {
                    // Swallow any IO or disconnection errors and log.
                    Logger.error(e.getMessage(), e);
                }
            }
            Thread.sleep(5000);
        }
    }
}
