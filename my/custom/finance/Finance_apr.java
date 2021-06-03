/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom.finance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

/**
 *
 * @author colino1804
 */
public class Finance_apr 
{  
    private double mortgage_remaining;
    private double interest_rate;

    //double mort_remain = 23274.67;
    //double mort_remain_new;
    //double apr_int_rate = 1.64;
    double day_interest_rate;
    //double day_interest_charge;
    private double month_repayment;
    private double day_int_charge;
    
    //public Finance_apr(double month_repayment, double mort_remain, double apr_int_rate)
    public Finance_apr()
    {
       
    }
    
    public void processMortgateInterestCalculation()
    {
       Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        
//        this.setMonthRepayment(month_repayment);
//        this.setInterestRate(apr_int_rate);
//        this.setMortgageRemaining(mort_remain);
        
//        SimpleDateFormat future_date = new SimpleDateFormat ("yyyy-MM-dd");
//        date2.;
        date2.set(2022, 2,31);
        int date_2_day_of_week = date2.get(Calendar.DAY_OF_WEEK); // 5 = Thursday, 0 = Saturday, etc.
       
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");        
        String date1_string = ft.format(date1.getTime());

        String date2_string = ft.format(date2.getTime());

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        System.out.println("** Calculations are based on a monthly repayment of £" + month_repayment + " **");
        System.out.println("== Calculating the days between " + date1_string + " and " + date2_string + "("+ date_2_day_of_week +") ==");
        System.out.println("== dayCount = " + dayCount);
        //Initializing the date formatter
        DateFormat Date = DateFormat.getDateInstance();
        //Initializing Calender Object
        Calendar cals = Calendar.getInstance();
        //Displaying the actual date
        System.out.println("The original Date: " + cals.getTime());
        //Using format() method for conversion
        String currentDate = Date.format(cals.getTime());
        System.out.println("Formatted Date: " + currentDate);
        
        System.out.println("== Lets do a Local date addition ==");
        SimpleDateFormat today_date = new SimpleDateFormat ("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(today_date.format(date1.getTime()));

        double mort_remain_new;
        //double apr_int_rate = 1.64;
        //double day_int_rate = (this.interest_rate / 365);
        //double day_int_charge;

        Float f = dayCount;
        int days_count = f.intValue();
        //int days_count = java.;
        LocalDate date_add = date.plusDays(days_count);
        LocalDate date_add_single;
        for(int i = 0; i <= days_count; i++)
        {
            date_add_single = date.plusDays(i);
            if(i != 0 && date_add_single.getDayOfMonth() == 1)
            {
                this.mortgage_remaining -= this.month_repayment; // deduct monthly mortgage repayment if it is the 1st of a month and not the first run of the loop (which may take into account first day, anyway.
            }
            this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
            
            //System.out.println(i +") On date "+date+" plus " + i + " days is "+date_add_single);
            //System.out.println(i +") On date "+ date_add_single +" the mortgate remaining is " + String.format("%.2f",this.mortgage_remaining) + " and for apr: " + this.interest_rate + ", the daily interest charge is "+ String.format("%.2f",this.day_int_charge) + "\nThe day of the month is " + date_add_single.getDayOfMonth());
            System.out.println(i +") On date "+ date_add_single +" the mortgate remaining is " + String.format("%.2f",this.mortgage_remaining) + " and for apr: " + this.interest_rate + ", the daily interest charge is "+ String.format("%.2f",this.day_int_charge));
            
            this.mortgage_remaining += this.day_int_charge;
            
            
            
            //mort_remain = 23274.67;
        }
        System.out.println("** Calculations were based on a monthly repayment of £" + month_repayment + " **");
        System.out.println("== Final amount of days: ==");
        System.out.println("Date "+date+" plus " + days_count + " days is "+date_add);          
    }
            
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
        this.day_interest_rate = (this.interest_rate / 365);
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
    
    // START | Questions for prompts in Desktop program or Android app, etc.
    public String promptForMonthlyRepayment()
    {
        return "Enter the monthly mortgage repayment";
    }
    public String promptForMortgateRemaining()
    {
        return "Enter the mortgage balance remaining";
    }
    public String promptForInterestRate()
    {
        return "Enter the interest rate";
    }
    /**
     * FALSE for end date and TRUE for start date
     * @param start_date
     * @return 
     */
    public String promptForDateOfCalculations(boolean start_date)
    {
        String start_end = start_date ?"start": "end";
        return "Enter the " + start_end + " date of the calculations";
    } 
    // END | Questions for prompts in Desktop program or Android app, etc.
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
