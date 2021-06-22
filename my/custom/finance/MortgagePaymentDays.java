package my.custom.finance;
import java.text.DecimalFormat;
import my.custom.finance.MortgagePaymentDay;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;



final public class MortgagePaymentDays
{
    
    //private MortgagePaymentDay[] mortgage_payment_day;
    final private TreeMap<String, MortgagePaymentDay> mortgage_payment_day = new TreeMap<>(); // Add overpayment date and amount    
    
    public MortgagePaymentDays()
    {
        
    }    
    public MortgagePaymentDays(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        this.addDay(date, mortgage_remaining, mortgage_interest_rate, day_interest_rate);
    }
    
    private String formatNumberToDecimalPlaces(double number)
    {
        DecimalFormat d = new DecimalFormat("#.00");
        //String string_places = "%." + decimal_places + "f";
        return d.format(number);
    }
    
    private void addDay(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        MortgagePaymentDay day = new MortgagePaymentDay(date, mortgage_remaining, mortgage_interest_rate, day_interest_rate);
        this.addDay(date, day);
    }
    private void addDay(String date, MortgagePaymentDay day)
    {       
        mortgage_payment_day.put(date, day);
    }
    
    private MortgagePaymentDay getDayObject(String date)
    {
        return mortgage_payment_day.get(date);
    }
    public int size()
    {
        return this.mortgage_payment_day.size();
    }
    
    /**
     * Alias of size method
     * @return MortgagePaymentDay.size()
     */
    public int count()
    {
        return this.size();
    }
}