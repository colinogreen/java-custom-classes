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
import java.text.ParseException;
import java.time.format.DateTimeParseException;

import my.custom.MessageDisplayer; // 2021-06-07 - New separate class to help display messages to console|GUI|Android app, etc.

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
    //private HashMap<String, String> mortgage_summary = new HashMap<>();
    final private TreeMap<String, String> mortgage_all_sorted = new TreeMap<>();
    final private TreeMap<String, String> mortgage_summary_sorted = new TreeMap<>();
    final private TreeMap<String, String> mortgage_milestones = new TreeMap<>();
    final private MessageDisplayer msgs;
    
    private boolean milestone_int_less_one_per_day = false;
    
    //public Finance_apr(double month_repayment, double mort_remain, double apr_int_rate)
    public Finance_apr()
    {
        this.msgs = new MessageDisplayer();
    }
    /**
     * 
     * @return 
     */
    public String getMessageString()
    {
        // Return messages to any app that needs them from external class.
        return msgs.getMessageString();
    }
    
    public void processMortgateInterestCalculation()
    {

        float dayCount = Duration.between(this.calendar_date_from.atStartOfDay(), this.calendar_date_to.atStartOfDay()).toDays();
        msgs.resetMessageString("** Calculations are based on a monthly repayment of £" + month_repayment + " **"); // clear any previous results and set string

        LocalDate date = this.calendar_date_from;

        LocalDate date_add = date.plusDays((int)dayCount);
        LocalDate date_add_single;
        for(int i = 0; i <= (int)dayCount; i++)
        {
            date_add_single = date.plusDays(i);
                       
            // If the loop is not at the very first item and it is the first day of the month, reduce the mortgage remaining by the mortgage amount.
            if(i != 0 && date_add_single.getDayOfMonth() == 1)
            {
                if(mortgage_remaining > this.month_repayment)
                {
                    this.mortgage_remaining -= this.month_repayment; // deduct monthly mortgage repayment if it is the 1st of a month and not the first run of the loop (which may take into account first day, anyway.
                    
                }
                else
                {
                    this.mortgage_remaining = (this.mortgage_remaining - this.mortgage_remaining); // Possibly the final mortgage payment, so, finish up!
                    
                }
                //** day_int_charge calc Moved here to see if it works better
                this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
                mortgage_summary_sorted.put(date_add_single.toString(),String.format("%.2f",this.mortgage_remaining) + " " + this.interest_rate + " " + String.format("%.2f",this.day_int_charge) );
                
                //mortgage_summary.put(date_add_single.toString(), "Count: ");
            }
            else
            {
                // Run day_int_charge calc only if not run on 1st day of months summary if statement.
                this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
            }
            this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
            mortgage_all_sorted.put(date_add_single.toString(),String.format("%.2f",this.mortgage_remaining) + " " + this.interest_rate + " " + String.format("%.2f",this.day_int_charge) );
            this.checkMortMilestoneIntRateLessThanOnePerDay(date_add_single.toString()); // check that mortgage int day rate is below 1 or not.
            //this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);

            this.mortgage_remaining += this.day_int_charge;
            if(this.mortgage_remaining <= 0)
            {
                break; // finish up, as the mortgage has been paid!
            }

        }
        msgs.setMessageString("** Calculations were based on a monthly repayment of £" + month_repayment + " **","\n");

        //this.setMessageString("\n","\n");
        msgs.setMessageString("== Final amount of days ==","\n");
        msgs.setMessageString("Date " + date + " plus " + (int)dayCount + " days is "+date_add,"\n"); 
        
        
    }
    
    private void checkMortMilestoneIntRateLessThanOnePerDay(String date)
    {
        Float int_charge = Float.valueOf(String.format("%.2f",this.day_int_charge));
        if(this.milestone_int_less_one_per_day == false && (int_charge < 1))
        {
            this.addMortgageMilestone(date, "The daily interest rate would go below 1 for the first time and would be " + int_charge + ".");
            this.milestone_int_less_one_per_day= true; // set true so that this is no longer activated.
        }
    }
    
    private void addMortgageMilestone(String date, String text)
    {
        this.mortgage_milestones.put(date, text);
    }
    
    /**
     * Method that supplies newline delimiter by default
     * @return msgs.getMessageString() of milestones
     */
    public String getMortgageMilestonesList()
    {
        return this.getMortgageMilestonesList("\n");
    }
    
    /**
     * Supply delimiter of your choice
     * @param delimiter
     * @return msgs.getMessageString() of milestones
     */
    public String getMortgageMilestonesList(String delimiter)
    {
        msgs.resetMessageString();
        this.mortgage_milestones.forEach((date, text)->{
        
            msgs.setMessageString(date + ": " + text, delimiter);
            
        });
        
        return msgs.getMessageString();
    }
    
    public void setMortgageSelectedEntries(boolean commandlinesummary)
    {
        if(commandlinesummary == true)
        {
            this.setMortgageDayFiguresSummary();
        }
        else
        {
            this.setMortgageDayFiguresAllEntries();
        }
    }
    
    private void setMortgageDayFiguresAllEntries()
    //private void setAllEntries()
    {
        msgs.resetMessageString(); // clear any previous results
        this.mortgage_all_sorted.forEach((key, value)->{
            
            String[] value_items = value.split(" ");
            this.setMortgageDayFiguresLine(key, value_items);

        });        
    }
    /**
     * Show each entry for summary
     */
    private void setMortgageDayFiguresSummary()
    //private void setCommandLineSummary()
    {
        msgs.resetMessageString(); // clear any previous results
        this.mortgage_summary_sorted.forEach((key, value)->{
            
            String[] value_items = value.split(" ");
            this.setMortgageDayFiguresLine(key, value_items);

        });                       
    }
    
    private void setMortgageDayFiguresLine(String key,String[] value_items)
    {
        msgs.setMessageString("Date: " + key);  // Start creating output
        //System.out.print("Date: " + value_items[0] + " ");
        msgs.setMessageString("Mortgage Remaining: " + value_items[0]," ");
        msgs.setMessageString("Mortgage Rate: " + value_items[1]," ");
        //this.setMessageString("Interest per day: " + value_items[2] + " | ");
        msgs.setMessageString("Interest per day: " + value_items[2]," ");        
    }
    /**
     * 
     * @param date 
     */
    public void setMortgageIndividualDateRecord(String date)
    {
        if(this.mortgage_all_sorted.containsKey(date))
        {
            String[] value_items = this.mortgage_all_sorted.get(date).split(" ");
            msgs.resetMessageString(); // clear any previous results
            this.setMortgageDayFiguresLine(date, value_items);
        }
        else
        {
            msgs.resetMessageString("Could not find a record for the date, " + date + "."); // clear any previous results
            msgs.setMessageString("The date must be between " + this.getDefaultDateFrom(), " "); // clear any previous results
            msgs.setMessageString("and " + this.getDefaultDateTo(), " "); // clear any previous results
        }

    }
    // * Validation | START //
    
    public boolean checkIfNumberIsADouble(String number)
    {
        return Pattern.matches(DOUBLE_PATTERN, number);
    }
    /**
     * 
     * @return true or false
     */
    public boolean isDateToGreaterThanDateFrom()
    {
        msgs.resetMessageString("++ Debug Method: Finance_apr::isDateToGreaterThanDateFrom. Start date: " 
                + this.calendar_date_to.toString() + " | End date: " +  this.calendar_date_from.toString() ); // clear any previous results and set string
        //return true;
        return this.calendar_date_to.isAfter(this.calendar_date_from);
    }
    
    public boolean isDateEnteredValid(String command)
    {
	    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
	    sdfrmt.setLenient(false);
	    /* Create Date object
	     * parse the string into date 
             */
	    try
	    {
	        sdfrmt.parse(command);
	    }
	    /* Date format is invalid */
	    catch (ParseException e)
	    {
	        //System.out.println(command+" is Invalid Date format");
	        return false;
	    }
	    /* Return true if date format is valid */
	    return true;
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

            
//    public void setDateToCalculateFrom(String df)
//    {
//        this.date_from = df;
//    }
//    
//    public String getDateToCalculateFrom()
//    {
//        return this.date_from;
//    }
//            
//            
//    public void setDateToCalculateTo(String dt)
//    {
//        this.date_to = dt;
//    }
//    
//    public String getDateToCalculateTo()
//    {
//        return this.date_to;
//    }
    
    public boolean setCalendarDate(String start_or_end_date, boolean start_date)
    {
        if(start_date == true)
        {
            return this.setCalendarDateFrom(start_or_end_date);
        }
        else
        {
            return this.setCalendarDateTo(start_or_end_date);
        }
    }
    
    private boolean setCalendarDateFrom(String date_from_string)
    {
        try
        {
            this.calendar_date_from = LocalDate.parse(date_from_string);
            return true;
        }
        catch(DateTimeParseException e)
        {
            return false;
        }
        
    }

    private boolean setCalendarDateTo(String date_to_string)
    {
        try
        {
            this.calendar_date_to = LocalDate.parse(date_to_string);
            return true;
        }
        catch(DateTimeParseException e)
        {
            return false;
        }
        
    }
    /**
     * @deprecated
     */
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
            msgs.resetMessageString("Note: default start date to: " + this.calendar_date_from.toString());  // clear any previous results and set string
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
            msgs.resetMessageString("Note: Setting default end date to: " + this.calendar_date_to.toString()); // clear any previous results and set string
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
           msgs.resetMessageString("** DEBUG: calendar_date_to WAS NULL **\n");  // clear any previous results and set string
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
    public String promptForMonthlyMortgageRepayment()
    {
        return "Enter the monthly mortgage repayment: ";
    }
    
    public String promptForMortgageRemaining()
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
