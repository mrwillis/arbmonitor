import com.opencsv.bean.CsvToBeanBuilder;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.coinbase.CoinbaseExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by julia on 7/3/2017.
 */
public class ArbMonitor {
    public HashMap<String, BigDecimal> UsdBalances;
    public HashMap<String, BigDecimal> BitcoinBalances;
    public HashMap<String, ExchangeInformation> ExchangeInfo;
    private Exchange BitstampExchange;
    private Exchange KrakenExchange;
    private Exchange CoinbaseExchange;
    private Exchange PoloinexExchange;
    private Exchange BitfinexExchange;
    private MarketDataService BitstampMarketDataService;
    private MarketDataService KrakenMarketDataService;
    private MarketDataService CoinbaseMarketDataService;
    private MarketDataService PoloniexMarketDataService;
    private MarketDataService BitfinexMarketDataService;

    public ArbMonitor() throws URISyntaxException, FileNotFoundException {
        this.BitstampExchange = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        this.KrakenExchange = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
        this.CoinbaseExchange = ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName());
        this.PoloinexExchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
        this.BitfinexExchange = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());

        this.BitstampMarketDataService = this.BitstampExchange.getMarketDataService();
        this.KrakenMarketDataService = this.KrakenExchange.getMarketDataService();
        this.CoinbaseMarketDataService = this.CoinbaseExchange.getMarketDataService();
        this.PoloniexMarketDataService = this.PoloinexExchange.getMarketDataService();
        this.BitfinexMarketDataService = this.BitfinexExchange.getMarketDataService();

        this.UsdBalances = new HashMap<>();
        this.BitcoinBalances = new HashMap<>();
        this.ExchangeInfo = new HashMap<>();

        URL resource = ArbMonitor.class.getResource("Exchange DB.csv");
        String path = null;
        path = Paths.get(resource.toURI()).toFile().getPath();
        List<ExchangeInformation> beans = new CsvToBeanBuilder(new FileReader(path)).withType(ExchangeInformation.class).build().parse();
        for (ExchangeInformation i : beans) {
            this.ExchangeInfo.put(i.exchange, i);
        }
    }

    /**
     * Gets the latest quotes from each of the exchanges. The entries may be less than the number of exchanges if data is not available.
     *
     * @return A HashMap with key: exchange and value: a Quote object
     */
    private HashMap<String, Quote> getCurrentQuotes() {
        HashMap<String, Quote> quotes = new HashMap<>();

        try {
            Ticker bitstampTicker = this.BitstampMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Bitstamp", new Quote(bitstampTicker.getBid(), bitstampTicker.getAsk()));
        } catch (ExchangeException | IOException e) {
            System.out.println("Bitstamp data not available");
        }

        try {
            Ticker krakenTicker = this.KrakenMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Kraken", new Quote(krakenTicker.getBid(), krakenTicker.getAsk()));
        } catch (ExchangeException | IOException e) {
            System.out.println("Kraken data not available");
        }

        try {
            Ticker coinbase = this.CoinbaseMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Coinbase", new Quote(coinbase.getBid(), coinbase.getAsk()));
        } catch (ExchangeException | IOException e) {
            System.out.println("Coinbase data not available");
        }

        try {
            Ticker poloinex = this.PoloniexMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Poloniex", new Quote(poloinex.getBid(), poloinex.getAsk()));
        } catch (ExchangeException | IOException e) {
            System.out.println("Poloinex data not available");
        }

        try {
            Ticker bitfinex = this.BitfinexMarketDataService.getTicker(CurrencyPair.BTC_USD);
            quotes.put("Bitfinex", new Quote(bitfinex.getBid(), bitfinex.getAsk()));
        } catch (ExchangeException | IOException e) {
            System.out.println("Bitfinex data not available");
        }
        return quotes;
    }

    public void monitor() throws InterruptedException{

        while (true) {
            Thread.sleep(5000);
            HashMap<String, Quote> quotes = getCurrentQuotes();
            for (Map.Entry<String, Quote> quoteOne : quotes.entrySet()) {
                for (Map.Entry<String, Quote> quoteTwo : quotes.entrySet()) {
                    BigDecimal spread;
                    BigDecimal bid = quoteOne.getValue().getBid();
                    BigDecimal ask = quoteTwo.getValue().getAsk();
                    ExchangeInformation toBuyExchange = this.ExchangeInfo.get(quoteTwo.getKey());
                    ExchangeInformation toSellExchange = this.ExchangeInfo.get(quoteOne.getKey());
                    spread = bid.subtract(ask).divide(ask, BigDecimal.ROUND_HALF_EVEN);

                    if (spread.subtract(toBuyExchange.getActiveCommission()).subtract(toSellExchange.getActiveCommission()).compareTo(new BigDecimal("0")) == 1) {
                        System.out.println("Opportunity exists");
                    }
                }
            }
        }
    }

}
