import java.math.BigDecimal;

/**
 * Created by julia on 7/3/2017.
 */
public class Quote {
    private BigDecimal Bid;
    private BigDecimal Ask;

    public BigDecimal getBid() {
        return Bid;
    }

    public void setBid(BigDecimal bid) {
        Bid = bid;
    }

    public BigDecimal getAsk() {
        return Ask;
    }

    public void setAsk(BigDecimal ask) {
        Ask = ask;
    }

    public Quote(BigDecimal bid, BigDecimal ask) {
        this.Bid = bid;
        this.Ask = ask;
    }
}
