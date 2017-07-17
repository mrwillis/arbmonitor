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
import org.knowm.xchange.quadrigacx.QuadrigaCxExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by julia on 7/3/2017.
 */
public class ArbMonitor {
    private static final Logger Logger = LoggerFactory.getLogger(ArbMonitor.class);

    private HashMap<String, FeeSchedule> ExchangeInfo;

    private List<MarketDataService> ExchangeServices;
    private List<Exchange> Exchanges;

    public ArbMonitor() throws URISyntaxException,
            IOException {

        this.Exchanges = new ArrayList<>();
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(GDAXExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(QuadrigaCxExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName()));
        this.Exchanges.add(ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()));

        this.ExchangeServices = new ArrayList<>();
        for (Exchange e:
             this.Exchanges) {
            this.ExchangeServices.add(e.getMarketDataService());
        }

        this.ExchangeInfo = this.RetrieveExchangeInformation();
    }

    private HashMap<String, FeeSchedule> RetrieveExchangeInformation() throws FileNotFoundException,
            URISyntaxException {

        List<String> exchanges = new ArrayList<>();
        exchanges.add("bitfinex");
        exchanges.add("bitstamp");
        exchanges.add("bittrex");
        exchanges.add("btce");
        exchanges.add("gdax");
        exchanges.add("kraken");
        exchanges.add("poloinex");
        exchanges.add("quadrigacx");

        HashMap<String, FeeSchedule> exchangeInfo = new HashMap<>();

        for (String exchange: exchanges) {
            URL resource = ArbMonitor.class.getResource(exchange + "_fees.csv");
            String path = null;
            path = Paths.get(resource.toURI()).toFile().getPath();
            List<FeeSchedule> beans = new CsvToBeanBuilder(new FileReader(path)).withType(FeeSchedule
                    .class).build().parse();
            for (FeeSchedule i : beans) {
                exchangeInfo.put(i.getExchange(), i);
            }
        }
        return exchangeInfo;
    }

    /**
     * Gets the latest quotes from each of the exchanges. The entries may be less than the number of exchanges if
     * data is not available.
     *
     * @return A HashMap with key: exchange and value: a Quote object
     */
    private HashMap<String, Quote> getCurrentQuotes() {
        HashMap<String, Quote> quotes = new HashMap<>();

        try {
            Ticker bitstampTicker = this.BitstampMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Bitstamp", new Quote(bitstampTicker.getBid(), bitstampTicker.getAsk()));
        } catch (Exception e) {
            Logger.warn("Bitstamp data not available");
        }

        try {
            Ticker krakenTicker = this.KrakenMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Kraken", new Quote(krakenTicker.getBid(), krakenTicker.getAsk()));
        } catch (Exception e) {
            Logger.warn("Kraken data not available");
        }

        try {
            Ticker gdax = this.GDAXMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("GDAX", new Quote(gdax.getBid(), gdax.getAsk()));
        } catch (Exception e) {
            Logger.warn("GDAX data not available");
        }

        try {
            Ticker poloinex = this.PoloniexMarketDataService.getTicker(new CurrencyPair("BTC", "USDT"));
            quotes.put("Poloniex", new Quote(poloinex.getBid(), poloinex.getAsk()));
        } catch (Exception e) {
            Logger.warn("Poloinex data not available");
        }

        try {
            Ticker bitfinex = this.BitfinexMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Bitfinex", new Quote(bitfinex.getBid(), bitfinex.getAsk()));
        } catch (Exception e) {
            Logger.warn("Bitfinex data not available");
        }

        try {
            Ticker quadrigacx = this.QuadrigaCxMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("QuadrigaCX", new Quote(quadrigacx.getBid(), quadrigacx.getAsk()));
        } catch (Exception e) {
            Logger.warn("QuadrigaCX data not available");
        }
        return quotes;
    }

    void Monitor() throws InterruptedException {
        Logger.error("hello");

        while (true) {
            HashMap<String, Quote> quotes = getCurrentQuotes();
            for (Map.Entry<String, Quote> quoteOne : quotes.entrySet()) {
                for (Map.Entry<String, Quote> quoteTwo : quotes.entrySet()) {
                    if (Objects.equals(quoteOne.getKey(), quoteTwo.getKey())) break;
                    BigDecimal spread, spreadFeeAdjusted;
                    BigDecimal bid = quoteOne.getValue().getBid();
                    BigDecimal ask = quoteTwo.getValue().getAsk();
                    FeeSchedule toBuyExchange = this.ExchangeInfo.get(quoteTwo.getKey());
                    FeeSchedule toSellExchange = this.ExchangeInfo.get(quoteOne.getKey());
                    spread = bid.subtract(ask).divide(ask, BigDecimal.ROUND_HALF_EVEN);
                    spreadFeeAdjusted = spread.subtract(toBuyExchange.getActiveCommission()).subtract(toSellExchange
                            .getActiveCommission());

                    if (spreadFeeAdjusted.compareTo(new BigDecimal("0")) == 1 && toSellExchange.allowsMarginTrading) {
                        Logger.warn("Opportunity exists. You can immediately buy on {} at LOWEST ASK: {} and short " +
                                        "sell on {} " +
                                        "at HIGHEST BID: {} and earn {}%. ",
                                toBuyExchange.exchange, ask, toSellExchange.exchange, bid, spreadFeeAdjusted.multiply
                                        (new BigDecimal("100.000")));
                    }
                }
            }
            Thread.sleep(5000);
        }
    }


}
