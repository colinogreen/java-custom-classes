/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom.finance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.time.*;

/**
 *
 * @author colino1804
 */
public class Finance_apr 
{  
    private double mortgage_remaining;
    private double interest_rate;
    
    //* For validation
    final static String DOUBLE_PATTERN = "[0-9]+(\\.){0,1}[0-9]*";
    final static String INTEGER_PATTERN = "\\d+";
    
    double day_interest_rate;
    //double day_interest_charge;
    private double month_repayment;
    private double day_int_charge;
    
    private String date_from;
    private String date_to;
    
    private LocalDate calendar_date_from;
    //private Calendar calendar_date_from;
    private LocalDate calendar_date_to;
    //private Calendar calendar_date_to;
    final static int DATE_PLUS_MONTHS = 6;
    
    //public Finance_apr(double month_repayment, double mort_remain, double apr_int_rate)
    public Finance_apr()
    {
       
    }
    
    public void processMortgateInterestCalculation()
    {
//        this.calendar_date_from = Calendar.getInstance();
//        this.calendar_date_to = Calendar.getInstance();

        //this.calendar_date_from = LocalDate.now();
        //this.calendar_date_to=  LocalDate.parse("2022-04-01");
        this.setDateRanges();
        //int date_2_day_of_week = calendar_date_to.get(Calendar.DAY_OF_WEEK); // 5 = Thursday, 0 = Saturday, etc.
        //String date_2_day_of_week = calendar_date_to.getDayOfWeek().toString(); // 5 = Thursday, 0 = Saturday, etc.
       
        //SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");        
        //String date1_string = ft.format(calendar_date_from.toString());
        //String date1_string = this.calendar_date_from .toString();

        //String date2_string = ft.format(calendar_date_to.toString());
        //String date2_string =this.calendar_date_to.toString();

        //long diff = calendar_date_to.getTimeInMillis() - calendar_date_from.getTimeInMillis();
        //long diff = calendar_date_to. - calendar_date_from.;

        //float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        float dayCount = Duration.between(this.calendar_date_from.atStartOfDay(), this.calendar_date_to.atStartOfDay()).toDays();
        System.out.println("** Calculations are based on a monthly repayment of £" + month_repayment + " **");
        //System.out.println("== Calculating the days between " + date1_string + " and " + date2_string + "("+ date_2_day_of_week +") ==");
        System.out.println("== dayCount = " + dayCount);
        //Initializing the date formatter
        DateFormat Date = DateFormat.getDateInstance();
        //Initializing Calender Object
        //Calendar cals = Calendar.getInstance();
        //Displaying the actual date
        System.out.println("The original Date: " + this.calendar_date_from.toString());
        //Using format() method for conversion
        //String currentDate = Date.format(cals.getTime());
        //System.out.println("Formatted Date: " + currentDate);
        
        System.out.println("== Lets do a Local date addition ==");
        SimpleDateFormat today_date = new SimpleDateFormat ("yyyy-MM-dd");
        //LocalDate date = LocalDate.parse(today_date.format(calendar_date_from.getTime()));
        LocalDate date = this.calendar_date_from;

        double mort_remain_new;
        //double apr_int_rate = 1.64;
        //double day_int_rate = (this.interest_rate / 365);
        //double day_int_charge;

        //Float f = dayCount;
        //int days_count = (int)dayCount;
        //int days_count = java.;
        LocalDate date_add = date.plusDays((int)dayCount);
        LocalDate date_add_single;
        for(int i = 0; i <= (int)dayCount; i++)
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

        }
        System.out.println("** Calculations were based on a monthly repayment of £" + month_repayment + " **");
        System.out.println("== Final amount of days: ==");
        System.out.println("Date " + date + " plus " + (int)dayCount + " days is "+date_add);          
    }
    
    // * Validation | START //
    
    public boolean checkIfNumberIsADouble(String number)
    {
        return Pattern.matches(DOUBLE_PATTERN, number);
    }
    // * Validation | END //
            
    public void setMonthRepayment(double amount)
    {
        this.month_repayment = amount;
    }
    
    public double getMonthRepayment()
    {
        return this.month_repayment;
    }

            
    public void setDateToCalculateFrom(String df)
    {
        this.date_from = df;
    }
    
    public String getDateToCalculateFrom()
    {
        return this.date_from;
    }
            
            
    public void setDateToCalculateTo(String dt)
    {
        this.date_to = dt;
    }
    
    public String getDateToCalculateTo()
    {
        return this.date_to;
    }
    
    private void setDateRanges()
    {
        String date_set1[] = this.date_from.split("-");
        //System.out.println(date_set[0] + "," + (Integer.valueOf(date_set[1]) -1) + "," + date_set[0]);
        if(date_set1.length == 3)
        {         
            this.calendar_date_from = LocalDate.parse(date_from);            
        }
        else
        {
            this.setDefaultDateFrom();
            System.out.println("Note: default start date to: " + this.calendar_date_from.toString());
        }

        String date_set2[] = this.date_to.split("-");
        if(date_set1.length == 3)
        {
            int year = Integer.valueOf(date_set2[0]);
            int month200 = (Integer.valueOf(date_set2[1]) -1);
            int day = Integer.valueOf(date_set2[2]);
            //this.calendar_date_to.of(year, month, day);            
            this.calendar_date_to = LocalDate.parse(date_to);            
        }
        else
        {
            this.setDefaultDateTo();
            System.out.println("Note: Setting default end date to: " + this.calendar_date_to.toString());
        }
    }
    
    public void setDefaultDateFrom()
    {
        this.calendar_date_from = LocalDate.now();
    }
    public void setDefaultDateTo()
    {
        if(this.calendar_date_to == null)
        {
           System.out.println("** DEBUG: calendar_date_to WAS NULL **\n");
           this.calendar_date_to = LocalDate.now().plusMonths(DATE_PLUS_MONTHS);
        }
        
    }
    
    public String getDefaultDateFrom()
    {
        return this.calendar_date_from.toString();
    }
    
    
    public String getDefaultDateTo()
    {
        return this.calendar_date_to.toString();
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
        return "Enter the monthly mortgage repayment: ";
    }
    
    public String promptForMortgateRemaining()
    {
        return "Enter the mortgage balance remaining: ";
    }
    
    public String promptForInterestRate()
    {
        return "Enter the interest rate: ";
    }
    
    /**
     * Overload!
     * FALSE for end date and TRUE for start date and can also add date format guide text, if necessary.
     * @param start_date
     * @param add_date_format
     * @return 
     */
    public String promptForDateOfCalculations(boolean start_date, boolean add_date_format)
    {
        String start_end = start_date ?"start": "end";
        String text = "Enter the " + start_end + " date of the calculations: ";
        
        text = add_date_format ? text + " (format: yyyy-mm-dd)": text;
        
        return text;
    }
    
    /**
     * Overload!
     * FALSE for end date and TRUE for start date
     * @param start_date
     * @return 
     */
    public String promptForDateOfCalculations(boolean start_date)
    {
        String start_end = start_date ?"start": "end";
        //String text = "Enter the " + start_end + " date of the calculations";
         
        return "Enter the " + start_end + " date of the calculation: ";
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
