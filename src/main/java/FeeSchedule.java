import com.opencsv.bean.CsvBindByName;

/**
 * Created by julia on 7/3/2017.
 */

public class FeeSchedule {
    @CsvBindByName(column = "Exchange")
    private String Exchange;
    @CsvBindByName(column = "Base")
    private String BaseCurrency;
    @CsvBindByName(column = "Quote")
    private String QuoteCurrency;
    @CsvBindByName(column = "Maker")
    private double MakerFee;

    public String getExchange() {
        return Exchange;
    }

    public void setExchange(String exchange) {
        Exchange = exchange;
    }

    public String getBaseCurrency() {
        return BaseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        BaseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return QuoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        QuoteCurrency = quoteCurrency;
    }

    public double getMakerFee() {
        return MakerFee;
    }

    public void setMakerFee(double makerFee) {
        MakerFee = makerFee;
    }

    public double getTakerFee() {
        return TakerFee;
    }

    public void setTakerFee(double takerFee) {
        TakerFee = takerFee;
    }

    @CsvBindByName(column = "Taker")
    public double TakerFee;
}
