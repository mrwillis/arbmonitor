import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

/**
 * Created by julia on 7/3/2017.
 */

public class ExchangeInformation {
    @CsvBindByName(column = "Exchange")
    public String exchange;
    @CsvBindByName(column = "Active_Commission")
    public BigDecimal activeCommission;
    @CsvBindByName(column = "Passive_Commission")
    public BigDecimal passiveCommission;
    @CsvBindByName(column = "Margin_Trading")
    public boolean allowsMarginTrading;
    @CsvBindByName(column = "Margin_Limit", required = false)
    public int marginLimit;
    @CsvBindByName(column = "Margin_Rate", required = false)
    public BigDecimal marginRate;
    @CsvBindByName(column = "USD_Deposit_Type")
    public String usdDepositType;
    @CsvBindByName(column = "USD_Withdrawal_Type")
    public String usdWithdrawalType;
    @CsvBindByName(column = "USD_Deposit_Pct")
    public BigDecimal usdDepositRate;
    @CsvBindByName(column = "USD_Withdrawal_Pct")
    public BigDecimal usdWithdrawalRate;
    @CsvBindByName(column = "USD_Deposit_Dollar")
    public BigDecimal usdDepositFlatFee;
    @CsvBindByName(column = "USD_Withdrawal_Dollar")
    public BigDecimal usdWithdrawalFlatFee;
    @CsvBindByName(column = "USD_Deposit_Time_Mins")
    public int usdDepositTimeMinutes;
    @CsvBindByName(column = "USD_Withdrawal_Time_Mins")
    public int usdWithdrawalTimeMinutes;
    @CsvBindByName(column = "USD_Deposit_Daily_Max")
    public int usdDepositDailyMax;
    @CsvBindByName(column = "USD_Withdrawal_Daily_Max")
    public int usdWithdrawalDailyMax;
    @CsvBindByName(column = "BTC_Deposit")
    public BigDecimal flatBitcoinDepositFee;
    @CsvBindByName(column = "BTC_Withdrawal")
    public BigDecimal flatBitcoinWithdrawalFee;
    @CsvBindByName(column = "BTC_Deposit_Time_Mins")
    public int bitcoinDepositTimeMinutes;
    @CsvBindByName(column = "BTC_Withdrawal_Time_Mins")
    public int bitcoinWithdrawalTimeMinutes;

    public BigDecimal getActiveCommission() {
        return activeCommission;
    }

    public void setActiveCommission(BigDecimal activeCommission) {
        this.activeCommission = activeCommission;
    }

    public BigDecimal getPassiveCommission() {
        return passiveCommission;
    }

    public void setPassiveCommission(BigDecimal passiveCommission) {
        this.passiveCommission = passiveCommission;
    }

    public boolean isAllowsMarginTrading() {
        return allowsMarginTrading;
    }

    public void setAllowsMarginTrading(boolean allowsMarginTrading) {
        this.allowsMarginTrading = allowsMarginTrading;
    }

    public int getMarginLimit() {
        return marginLimit;
    }

    public void setMarginLimit(int marginLimit) {
        this.marginLimit = marginLimit;
    }

    public BigDecimal getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(BigDecimal marginRate) {
        this.marginRate = marginRate;
    }

    public String getUsdDepositType() {
        return usdDepositType;
    }

    public void setUsdDepositType(String usdDepositType) {
        this.usdDepositType = usdDepositType;
    }

    public String getUsdWithdrawalType() {
        return usdWithdrawalType;
    }

    public void setUsdWithdrawalType(String usdWithdrawalType) {
        this.usdWithdrawalType = usdWithdrawalType;
    }

    public BigDecimal getUsdDepositRate() {
        return usdDepositRate;
    }

    public void setUsdDepositRate(BigDecimal usdDepositRate) {
        this.usdDepositRate = usdDepositRate;
    }

    public BigDecimal getUsdWithdrawalRate() {
        return usdWithdrawalRate;
    }

    public void setUsdWithdrawalRate(BigDecimal usdWithdrawalRate) {
        this.usdWithdrawalRate = usdWithdrawalRate;
    }

    public BigDecimal getUsdDepositFlatFee() {
        return usdDepositFlatFee;
    }

    public void setUsdDepositFlatFee(BigDecimal usdDepositFlatFee) {
        this.usdDepositFlatFee = usdDepositFlatFee;
    }

    public BigDecimal getUsdWithdrawalFlatFee() {
        return usdWithdrawalFlatFee;
    }

    public void setUsdWithdrawalFlatFee(BigDecimal usdWithdrawalFlatFee) {
        this.usdWithdrawalFlatFee = usdWithdrawalFlatFee;
    }

    public int getUsdDepositTimeMinutes() {
        return usdDepositTimeMinutes;
    }

    public void setUsdDepositTimeMinutes(int usdDepositTimeMinutes) {
        this.usdDepositTimeMinutes = usdDepositTimeMinutes;
    }

    public int getUsdWithdrawalTimeMinutes() {
        return usdWithdrawalTimeMinutes;
    }

    public void setUsdWithdrawalTimeMinutes(int usdWithdrawalTimeMinutes) {
        this.usdWithdrawalTimeMinutes = usdWithdrawalTimeMinutes;
    }

    public int getUsdDepositDailyMax() {
        return usdDepositDailyMax;
    }

    public void setUsdDepositDailyMax(int usdDepositDailyMax) {
        this.usdDepositDailyMax = usdDepositDailyMax;
    }

    public int getUsdWithdrawalDailyMax() {
        return usdWithdrawalDailyMax;
    }

    public void setUsdWithdrawalDailyMax(int usdWithdrawalDailyMax) {
        this.usdWithdrawalDailyMax = usdWithdrawalDailyMax;
    }

    public BigDecimal getFlatBitcoinDepositFee() {
        return flatBitcoinDepositFee;
    }

    public void setFlatBitcoinDepositFee(BigDecimal flatBitcoinDepositFee) {
        this.flatBitcoinDepositFee = flatBitcoinDepositFee;
    }

    public BigDecimal getFlatBitcoinWithdrawalFee() {
        return flatBitcoinWithdrawalFee;
    }

    public void setFlatBitcoinWithdrawalFee(BigDecimal flatBitcoinWithdrawalFee) {
        this.flatBitcoinWithdrawalFee = flatBitcoinWithdrawalFee;
    }

    public int getBitcoinDepositTimeMinutes() {
        return bitcoinDepositTimeMinutes;
    }

    public void setBitcoinDepositTimeMinutes(int bitcoinDepositTimeMinutes) {
        this.bitcoinDepositTimeMinutes = bitcoinDepositTimeMinutes;
    }

    public int getBitcoinWithdrawalTimeMinutes() {
        return bitcoinWithdrawalTimeMinutes;
    }

    public void setBitcoinWithdrawalTimeMinutes(int bitcoinWithdrawalTimeMinutes) {
        this.bitcoinWithdrawalTimeMinutes = bitcoinWithdrawalTimeMinutes;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
