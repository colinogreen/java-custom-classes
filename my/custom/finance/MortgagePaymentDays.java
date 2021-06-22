package my.custom.finance;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * This class processes individual MortgagePaymentDay class entries.
 * @author Colin M.
 */
final public class MortgagePaymentDays
{
    final private TreeMap<String, MortgagePaymentDay> mortgage_payment_day = new TreeMap<>(); // Add overpayment date and amount    
    String day_list;
    String range_list;
    
    private String formatNumberToDecimalPlaces(double number)
    {
        DecimalFormat d = new DecimalFormat("0.00");
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
    
    /**
     * If target MortgagePaymentDay entry exists for the day we can safely call methods such as getDay
     * @param date
     * @return 
     */
    public boolean isMortgagePaymentDayExists(String date)
    {
        return this.mortgage_payment_day.containsKey(date);
    }
    
    public MortgagePaymentDay getDay(String date)
    {
        return mortgage_payment_day.get(date); // Return the MortgagePaymentDay class/data for the supplied date
    }
    
    public String getValidDateRange()
    {
        String date_range = mortgage_payment_day.firstKey();
        return date_range += " - " +mortgage_payment_day.lastKey();
    }
    
    public String getMortgagePaymentDayIndividualData(String date, String delimiter) 
    {
        //String string = "No data found for the date, "+ date;
        this.resetDayList();
        if(this.isMortgagePaymentDayExists(date))
        {
            this.setDayList(this.getMortgagePaymentDayString(this.getDayObject(date)), delimiter);
        }
        
        return this.getDayList();
    }
    public String getMortgagePaymentDayData(boolean first_day_of_month_only, String delimiter)            
    {
        this.resetDayList();
        mortgage_payment_day.forEach((date, day)->
        {
            if(first_day_of_month_only && day.isMortgageRepaymentDay())
            {
                //System.out.println("Setting day string for date, " + date + " | " + getMortgagePaymentDayString(day));
                this.setDayList( this.getMortgagePaymentDayString(day), "\n");
            }
            else if(!first_day_of_month_only)
            {
                this.setDayList( this.getMortgagePaymentDayString(day), "\n");
            }
            
        });
        //System.out.println("**Debugging method getMortgagePaymentDayData **\n"); System.exit(0);
        return this.getDayList();
    }
    
    public String getMortgagePaymentDataForRange(String date_from, String date_to)
    {
            String date_to_full_range = LocalDate.parse(date_to).plusDays(1).toString(); // * make sure to include final date in the range
            this.resetDayList();
            this.mortgage_payment_day.subMap(date_from, date_to_full_range).forEach((key, day)->{

                this.setDayList(this.getMortgagePaymentDayString(day), "\n");
            }); 
            
            return this.getDayList();
    }
    
    private void resetDayList()
    {
        this.day_list = "";
    }
    private void setDayList(String string, String delimiter)
    {
        this.day_list += string + delimiter;
    }
    
    /**
     * Method version without a delimiter
     * @param string 
     */
    private void setDayList(String string)
    {
        this.day_list += string;
    }
    
    private String getDayList()
    {
        return this.day_list;
    }
    
    /**
     * 
     * @param entry
     * @return data
     */
    public String getMortgagePaymentDayString(MortgagePaymentDay entry)
    {
        //String data = "";
        String data = "Date: " + entry.getDate() + "\t";
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