package my.custom.finance;

/**
 * Individual Mortgage payment day class
 * @author Colin M.
 */
final class MortgagePaymentDay
{
    private String date;
    private double mortgage_remaining;
    private float mortgage_interest_rate;
    private double day_interest_rate;
    
    private boolean first_of_the_month = false;
    private boolean overpayment_day = false;

    /**
     * Default constructor without needing params
     */
    public MortgagePaymentDay()
    {

    }
    /**
     * 
     * @param date
     * @param mortgage_remaining
     * @param mortgage_interest_rate
     * @param day_interest_rate 
     */
    public MortgagePaymentDay(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        this.setDate(date);
        this.setMortgageRemaining(mortgage_remaining);
        this.setMortgageInterestRate(mortgage_interest_rate);
        this.setDayInterestRate(day_interest_rate);
    }
    /**
     * Set main data with this method if not using the constructor.
     * @param date
     * @param mortgage_remaining
     * @param mortgage_interest_rate
     * @param day_interest_rate 
     */
    public void setData(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        this.setDate(date);
        this.setMortgageRemaining(mortgage_remaining);
        this.setMortgageInterestRate(mortgage_interest_rate);
        this.setDayInterestRate(day_interest_rate);
    }
    
    public void setFirstOfTheMonth()
    {
        this.first_of_the_month = true;
    }
    
    public void setOverpaymentDay()
    {
        this.overpayment_day = true;
    }
    
    public boolean isFirstOfTheMonth()
    {
        return this.first_of_the_month;
    }
    
    public boolean isOverpaymentDay()
    {
        return this.overpayment_day;
    }
    
    private void setDate(String date)
    {
        this.date = date;
    }
    
    private void setMortgageRemaining(double remaining)
    {
        this.mortgage_remaining = remaining;
    }
    
    private void setMortgageInterestRate(float rate)
    {
        this.mortgage_interest_rate = rate;
    }
    
    private void setDayInterestRate(double rate)
    {
        this.day_interest_rate = rate;
    }
    
    public String getDate()
    {
        return this.date;
    }
    
    public double getMortgageRemaining()
    {
        return this.mortgage_remaining;
    }
    
    public float getMortgageInterestRate()
    {
        return this.mortgage_interest_rate;
    }
    
    public double getDayInterestRate()
    {
        return this.day_interest_rate;
    }
}