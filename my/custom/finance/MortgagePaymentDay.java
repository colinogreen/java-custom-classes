package my.custom.finance;


final class MortgagePaymentDay
{
    private String date;
    private double mortgage_remaining;
    private float mortgage_interest_rate;
    private double day_interest_rate;
    //private;
    // date mortgage_remaining mortgage_interest_rate day_interest_rate
    //setDate setMortgageRemaining setMortgageInterestRate setDayInterestRate
    //getDate getMortgageRemaining getMortgageInterestRate getDayInterestRate
            
    // String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate
    public MortgagePaymentDay(String date, double mortgage_remaining, float mortgage_interest_rate, double day_interest_rate)
    {
        this.setDate(date);
        this.setMortgageRemaining(mortgage_remaining);
        this.setMortgageInterestRate(mortgage_interest_rate);
        this.setDayInterestRate(day_interest_rate);
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