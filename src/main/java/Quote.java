import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;

/**
 * Created by julia on 7/3/2017.
 */
public class Quote {

    private BigDecimal Bid;
    private BigDecimal Ask;
    private CurrencyPair Pair;
    private String Exchange;

    private BigDecimal TakerFee;

    public Quote(BigDecimal bid, BigDecimal ask, CurrencyPair pair, String exchange, BigDecimal takerFee) {
        Bid = bid;
        Ask = ask;
        Pair = pair;
        Exchange = exchange;
        TakerFee = takerFee;
    }

    public BigDecimal getTakerFee() {
        return TakerFee;
    }

    public void setTakerFee(BigDecimal takerFee) {
        TakerFee = takerFee;
    }

    CurrencyPair getPair() {
        return Pair;
    }

    public void setPair(CurrencyPair pair) {
        Pair = pair;
    }

    public String getExchange() {
        return Exchange;
    }

    public void setExchange(String exchange) {
        Exchange = exchange;
    }

    BigDecimal getBid() {
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

    public boolean isSamePair(Quote other) {

        if (other.getPair() == this.getPair()) { // Directly compare base to base and counter to counter
            return true;
        } else if (other.getPair() == new CurrencyPair(other.getPair().counter, other.getPair().base)) { // Check
            // reverse
            return true;
        }
        return false;
    }
}
