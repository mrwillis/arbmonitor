import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by julia on 7/22/2017.
 */
public class ArbitrageOpportunity {
    private Date AsOf;
    private String BuyExchange;
    private String BuyExchangePair;
    private BigDecimal BuyPrice;
    private BigDecimal BuyFee;
    private String SellExchange;
    private String SellExchangePair;
    private BigDecimal SellPrice;
    private BigDecimal SellFee;
    private BigDecimal Spread;

    ArbitrageOpportunity(Quote buyQuote, Quote sellQuote) {
        this.AsOf = new Date();
        this.BuyExchange = buyQuote.getExchange();
        this.BuyExchangePair = buyQuote.getPair().toString();
        this.SellExchange = sellQuote.getExchange();
        this.SellExchangePair = sellQuote.getPair().toString();
        this.BuyPrice = buyQuote.getAsk();
        this.SellPrice = sellQuote.getBid();
    }

    public Date getAsOf() {
        return AsOf;
    }

    public void setAsOf(Date asOf) {
        AsOf = asOf;
    }

    public String getBuyExchange() {
        return BuyExchange;
    }

    public void setBuyExchange(String buyExchange) {
        BuyExchange = buyExchange;
    }

    public String getBuyExchangePair() {
        return BuyExchangePair;
    }

    public void setBuyExchangePair(String buyExchangePair) {
        BuyExchangePair = buyExchangePair;
    }

    public String getSellExchange() {
        return SellExchange;
    }

    public void setSellExchange(String sellExchange) {
        SellExchange = sellExchange;
    }

    public String getSellExchangePair() {
        return SellExchangePair;
    }

    public void setSellExchangePair(String sellExchangePair) {
        SellExchangePair = sellExchangePair;
    }

    public BigDecimal getBuyPrice() {
        return BuyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        BuyPrice = buyPrice;
    }

    public BigDecimal getSellPrice() {
        return SellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        SellPrice = sellPrice;
    }

    public BigDecimal getBuyFee() {
        return BuyFee;
    }

    public void setBuyFee(BigDecimal buyFee) {
        BuyFee = buyFee;
    }

    public BigDecimal getSellFee() {
        return SellFee;
    }

    public void setSellFee(BigDecimal sellFee) {
        SellFee = sellFee;
    }

    public BigDecimal getSpread() {
        return Spread;
    }

    public void setSpread(BigDecimal spread) {
        Spread = spread;
    }
}
