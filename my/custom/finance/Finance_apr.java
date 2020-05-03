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
    
    public void anotherHello()
    {
        System.out.println("Hello world from Finance_apr class!");
        System.out.println("The mortgage remaining has been set to been set to " + getMortgageRemaining());
        System.out.println("The interest rate has been set to " + getInterestRate());
    }
    
    public void setMortgageRemaining(double amount)
    {
        this.mortgage_remaining = amount;
    }
    
    private double getMortgageRemaining()
    {
        return this.mortgage_remaining;
    }
    
    public void setInterestRate(double interest_rate)
    {
        this.interest_rate = interest_rate;
    }
    
    private double getInterestRate()
    {
        return this.interest_rate;
    }
}
