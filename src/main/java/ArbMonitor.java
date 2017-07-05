import com.opencsv.bean.CsvToBeanBuilder;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
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
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by julia on 7/3/2017.
 */
public class ArbMonitor {
    public static final Logger Logger = LoggerFactory.getLogger(ArbMonitor.class);

    public HashMap<String, BigDecimal> UsdBalances;
    public HashMap<String, BigDecimal> BitcoinBalances;
    public HashMap<String, ExchangeInformation> ExchangeInfo;
    private Exchange BitstampExchange;
    private Exchange KrakenExchange;
    private Exchange GDAXExchange;
    private Exchange PoloinexExchange;
    private Exchange BitfinexExchange;
    private Exchange QuadrigaCxExchange;
    private MarketDataService BitstampMarketDataService;
    private MarketDataService KrakenMarketDataService;
    private MarketDataService GDAXMarketDataService;
    private MarketDataService PoloniexMarketDataService;
    private MarketDataService BitfinexMarketDataService;
    private MarketDataService QuadrigaCxMarketDataService;

    public ArbMonitor() throws URISyntaxException, FileNotFoundException {
        this.BitstampExchange = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        this.KrakenExchange = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
        this.GDAXExchange = ExchangeFactory.INSTANCE.createExchange(GDAXExchange.class.getName());
        this.PoloinexExchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
        this.BitfinexExchange = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());
        this.QuadrigaCxExchange = ExchangeFactory.INSTANCE.createExchange(QuadrigaCxExchange.class.getName());

        this.BitstampMarketDataService = this.BitstampExchange.getMarketDataService();
        this.KrakenMarketDataService = this.KrakenExchange.getMarketDataService();
        this.GDAXMarketDataService = this.GDAXExchange.getMarketDataService();
        this.PoloniexMarketDataService = this.PoloinexExchange.getMarketDataService();
        this.BitfinexMarketDataService = this.BitfinexExchange.getMarketDataService();
        this.QuadrigaCxMarketDataService = this.QuadrigaCxExchange.getMarketDataService();

        this.UsdBalances = new HashMap<>();
        this.BitcoinBalances = new HashMap<>();
        this.ExchangeInfo = new HashMap<>();

        URL resource = ArbMonitor.class.getResource("Exchange DB.csv");
        String path = null;
        path = Paths.get(resource.toURI()).toFile().getPath();
        List<ExchangeInformation> beans = new CsvToBeanBuilder(new FileReader(path)).withType(ExchangeInformation
                .class).build().parse();
        for (ExchangeInformation i : beans) {
            this.ExchangeInfo.put(i.exchange, i);
        }
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

    void monitor() throws InterruptedException {

        while (true) {
            HashMap<String, Quote> quotes = getCurrentQuotes();
            for (Map.Entry<String, Quote> quoteOne : quotes.entrySet()) {
                for (Map.Entry<String, Quote> quoteTwo : quotes.entrySet()) {
                    if (Objects.equals(quoteOne.getKey(), quoteTwo.getKey())) break;
                    BigDecimal spread, spreadFeeAdjusted;
                    BigDecimal bid = quoteOne.getValue().getBid();
                    BigDecimal ask = quoteTwo.getValue().getAsk();
                    ExchangeInformation toBuyExchange = this.ExchangeInfo.get(quoteTwo.getKey());
                    ExchangeInformation toSellExchange = this.ExchangeInfo.get(quoteOne.getKey());
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
