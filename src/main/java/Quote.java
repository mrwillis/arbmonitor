import java.math.BigDecimal;

/**
 * Created by julia on 7/3/2017.
 */
public class Quote {

    private String Base;
    private String Quote;
    private BigDecimal Bid;
    private BigDecimal Ask;

    public Quote(String base, String quote, BigDecimal bid, BigDecimal ask) {
        Base = base;
        Quote = quote;
        Bid = bid;
        Ask = ask;
    }

    public String getBase() {
        return Base;
    }

    public void setBase(String base) {
        Base = base;
    }

    public String getQuote() {
        return Quote;
    }

    public void setQuote(String quote) {
        Quote = quote;
    }

    private BigDecimal getBid() {
        return Bid;
    }

    public void setBid(BigDecimal bid) {
        Bid = bid;
    }

    private BigDecimal getAsk() {
        return Ask;
    }

    public void setAsk(BigDecimal ask) {
        Ask = ask;
    }
}
