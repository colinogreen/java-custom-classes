package my.custom.finance;
import java.text.DecimalFormat;
import my.custom.finance.MortgagePaymentDay;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * This class processes individual MortgagePaymentDay class entries.
 * @author Colin M.
 */
final public class MortgagePaymentDays
{
    
    //private MortgagePaymentDay[] mortgage_payment_day;
    final private TreeMap<String, MortgagePaymentDay> mortgage_payment_day = new TreeMap<>(); // Add overpayment date and amount    
    String day_list;
    
    public MortgagePaymentDays()
    {
        
    }    
//    public MortgagePaymentDays(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
//    {
//        this.addDay(date, mortgage_remaining, mortgage_interest_rate, day_interest_rate);
//    }
    
    private String formatNumberToDecimalPlaces(double number)
    {
        DecimalFormat d = new DecimalFormat("0.00");
        //String string_places = "%." + decimal_places + "f";
        return d.format(number);
    }
    
    public void addDay(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        MortgagePaymentDay day = new MortgagePaymentDay(date, mortgage_remaining, mortgage_interest_rate, day_interest_rate);
        this.addDay(date, day);
    }
    public void addDay(String date, MortgagePaymentDay day)
    {       
        mortgage_payment_day.put(date, day);
    }
    
    private MortgagePaymentDay getDayObject(String date)
    {
        return mortgage_payment_day.get(date);
    }
    
    public String getMortgagePaymentDayData(boolean first_day_of_month_only, String delimiter)            
    {
        mortgage_payment_day.forEach((date, day)->
        {
            if(first_day_of_month_only && day.isFirstOfTheMonth())
            {
                //System.out.println("Setting day string for date, " + date + " | " + getMortgagePaymentDayString(day));
                this.setDayList( getMortgagePaymentDayString(day),delimiter);
            }
            else if(!first_day_of_month_only)
            {
                this.setDayList( getMortgagePaymentDayString(day),delimiter);
            }
            
        });
        //System.out.println("**Debugging method getMortgagePaymentDayData **\n"); System.exit(0);
        return this.getDayList();
    }
    
    private void setDayList(String string, String delimiter)
    {
        this.day_list += string + delimiter;
    }
    
    private String getDayList()
    {
        return this.day_list;
    }
    
    public String getMortgagePaymentDayString(MortgagePaymentDay entry)
    {
        String data = "";
        //MortgagePaymentDay entry = this.getDayObject(date);
        data += "Date: " + entry.getDate() + "\t";
        data += "Mortgage Remaining: " + this.formatNumberToDecimalPlaces(entry.getMortgageRemaining()) + "\t";
        data += "Overall Int rate: " + entry.getMortgageInterestRate() + "\t";
        data += "Day Int rate: " +  this.formatNumberToDecimalPlaces(entry.getDayInterestRate());
        
        return data;
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