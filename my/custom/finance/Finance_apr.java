/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom.finance;

/**
 *
 * @author colino1804
 */
public class Finance_apr 
{  
    private double mortgage_remaining;
    private double interest_rate;

        //double mort_remain = 23274.67;
        double mort_remain_new;
        //double apr_int_rate = 1.64;
        double day_interest_rate;
        double day_interest_charge;
        double month_repayment;  
        
    
    public void setMonthRepayment(double amount)
    {
        this.month_repayment = amount;
    }
    
    public double getMonthRepayment()
    {
        return this.month_repayment;
    }
  
    /**
     * Note: This gets set by call to setInterestRate method
     */
    private void setDayInterestRate()
    {
        this.day_interest_rate = (interest_rate / 365);
    }
    
    private double getDayInterestRate()
    {
        return this.day_interest_rate;
    }
    
    /**
     * Testing method:
     * Used in original pre-2021 mortgage-calculator
     */
    public void anotherHello()
    {
        System.out.println("Hello world from Finance_apr class!");
        System.out.println("The mortgage remaining has been set to been set to " + getMortgageRemaining());
        System.out.println("The interest rate has been set to " + getInterestRate());
    }
    
    /**
     * Used in original pre-2021 mortgage-calculator
     * @param amount 
     */
    public void setMortgageRemaining(double amount)
    {
        this.mortgage_remaining = amount;
    }

    
    /**
     * Used in original pre-2021 mortgage-calculator
     * @return 
     */
    private double getMortgageRemaining()
    {
        return this.mortgage_remaining;
    }
    
    /**
     * Used in original pre-2021 mortgage-calculator
     * @param interest_rate 
     */
    public void setInterestRate(double interest_rate)
    {
        this.interest_rate = interest_rate;
        this.setDayInterestRate();
    }
    /**
     * Used in original pre-2021 mortgage-calculator
     * @return 
     */
    private double getInterestRate()
    {
        return this.interest_rate;
    }
}
