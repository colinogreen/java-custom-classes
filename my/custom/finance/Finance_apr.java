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
    private double mortgage_remaining_initial;
    private double mortgage_remaining;
    private double interest_rate;
    
    //* For validation
    final static String DOUBLE_PATTERN = "[0-9]+(\\.){0,1}[0-9]*";
    final static String INTEGER_PATTERN = "\\d+";
    final public double MAX_MORTGAGE_INT_RATE = 18.0; // Based on highest UK base rate ever: 17%
    final public int MAX_MORTGAGE_TERM = 40; // Recent UK max
    final public int MAX_MORTGAGE_LOAN= 500000; // Recent UK max loan: around 411,000
    final public int MAX_MONTHLY_REPAYMENT = 3000; // Hope should never go higher than that
    
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
    final private HashMap<String, String> error_list = new HashMap<>();
    
    private String error_list_messages;
    final private MessageDisplayer msgs;
    
    private boolean milestone_int_less_one_per_day = false;
    private boolean milestone_25percent_amount_paid = false;
    private boolean milestone_50percent_amount_paid = false;
    private boolean milestone_75percent_amount_paid = false;
    
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
    
    public String getMortgageInputSummary()
    {
        String msg = "----------------------------------------------------------------\n";
        msg += "Note: These calculations are based on a the following figures:\n";
        msg += "Initial Mortgage payment remaining: " + this.getMortgageRemainingInitial() + "\n";
        msg += "Repayment amount: " + this.getMonthRepayment() + "\n";
        msg += "Mortgage interest rate: " + this.getInterestRate() + "\n";
        msg += "Date range: " + this.getCalendarDateFrom() + " - " + getCalendarDateTo() +"\n";
        msg += "----------------------------------------------------------------\n";
        
        return msg;
    }
    public void processMortgateInterestCalculation()
    {

        float dayCount = Duration.between(this.calendar_date_from.atStartOfDay(), this.calendar_date_to.atStartOfDay()).toDays();
        msgs.resetMessageString("** Calculations are based on a monthly repayment of £" + month_repayment + " **"); // clear any previous results and set string
        this.mortgage_remaining_initial = this.mortgage_remaining;
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
                
                 // check that mortgage int day rate is below 1 or not and make a note for the milestones report
                this.checkMortMilestoneIntRateLessThanOnePerDay(date_add_single.toString());
                 // Check whether a certain percentage of the entered mortgage total has been paid back and make a note for milestones
                this.checkMortMilestonePercentAmountPaid(date_add_single.toString());

            }
            else
            {
                // Run day_int_charge calc only if not run on the 'first day of month' section.
                this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
            }
            this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining / 100);
            mortgage_all_sorted.put(date_add_single.toString(),String.format("%.2f",this.mortgage_remaining) + " " + this.interest_rate + " " + String.format("%.2f",this.day_int_charge) );
           
            if(this.mortgage_remaining <= 0)
            {
                break; // finish up, as the mortgage has been paid!
            }
            
            this.mortgage_remaining += this.day_int_charge;

        }
        msgs.setMessageString("** Calculations were based on a monthly repayment of £" + month_repayment + " **","\n");

        msgs.setMessageString("== Final amount of days ==","\n");
        msgs.setMessageString("Date " + date + " plus " + (int)dayCount + " days is "+date_add,"\n"); 
        
        
    }
    
    private void checkMortMilestoneIntRateLessThanOnePerDay(String date)
    {
        Float int_charge = Float.valueOf(String.format("%.2f",this.day_int_charge));
        if(this.milestone_int_less_one_per_day == false && (int_charge < 1))
        {
            this.addMortgageMilestone(date, "The daily interest rate would go below 1 for the first time and would be " + int_charge
                    + " (with " + Float.valueOf(String.format("%.2f",this.mortgage_remaining)) + " remaining on the mortgage total supplied).");
            this.milestone_int_less_one_per_day= true; // set true so that this is no longer activated.
        }
    }
    
    private void checkMortMilestonePercentAmountPaid(String date)
    {

        if(this.milestone_25percent_amount_paid == false )
        {
            
            double mortgage_percent_less_paid = this.mortgage_remaining_initial - (this.mortgage_remaining_initial * 25 / 100 );
            if( this.mortgage_remaining < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining));
                this.addMortgageMilestone(date, "The mortgage remaining is now at least 25 percent less than the initial amount (" + mort_remain + ")");
                this.milestone_25percent_amount_paid = true;               
            }

        }
        
        else if(this.milestone_50percent_amount_paid == false )
        {
            double mortgage_percent_less_paid = this.mortgage_remaining_initial - (this.mortgage_remaining_initial * 50 / 100 );
            if( this.mortgage_remaining < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining));
                this.addMortgageMilestone(date, "The mortgage figure remaining is now at least 50 percent less than the initial amount (" + mort_remain + ")");
                this.milestone_50percent_amount_paid = true;                
            }            

        }
        
        else if(this.milestone_75percent_amount_paid == false )
        {
            double mortgage_percent_less_paid = this.mortgage_remaining_initial - (this.mortgage_remaining_initial * 75 / 100 );
            if( this.mortgage_remaining < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining));
                this.addMortgageMilestone(date, "The mortgage amount remaining is now at least 75 percent less than the initial amount (" + mort_remain + ")");
                this.milestone_75percent_amount_paid = true;                
            }            

            //(percent ==25)?this.milestone_25percent_amount_paid = true:this.milestone_50percent_amount_paid;
        }
        // milestone_25percent_amount_paid // this.mortgage_remaining_initial this.mortgage_remaining
    }
    /**
     * Overload with default newline delimiter
     * @param date
     * @param text 
     */
    private void addMortgageMilestone(String date, String text)
    {
        
        this.addMortgageMilestone(date, text, "\n");
        
    }
    /**
     * 
     * @param date
     * @param text
     * @param delimiter 
     */
    private void addMortgageMilestone(String date, String text, String delimiter)
    {
        
        if(!this.mortgage_milestones.containsKey(date))
        {
            this.mortgage_milestones.put(date, text);
        }
        else
        {
            String orig_text = this.mortgage_milestones.get(date);
            this.mortgage_milestones.put(date, orig_text + delimiter + text);
        }
        
    }
    
    public int getMortgageMilestonesCount()
    {
        return this.mortgage_milestones.size();
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
        if(!mortgage_milestones.isEmpty())
        {
            this.mortgage_milestones.forEach((date, text)->{

                msgs.setMessageString(date + ": " + text, delimiter);

            });

            return msgs.getMessageString();            
        }
        
        return "Note: There are no mortgage milestones record." + delimiter;

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
    public void getMortgageDayFiguresRangeFromTo(String date_from, String date_to)
    {
        msgs.resetMessageString(); // clear any previous results
        //this.mortgage_all_sorted.entrySet(date_from, date_to).;
        boolean process_method = true;
        this.resetErrorList();
        if(!this.isDateEnteredValid(date_from))
        {
            //this.msgs.setMessageString("The date from (" + date_from + ") is invalid. Use format: yyyy-mm-dd");
            this.setErrorListItem("date_from", "The date from (" + date_from + ") is invalid. Use format: yyyy-mm-dd");
            process_method = false;
        }
        
        if(!this.isDateEnteredValid(date_to))
        {
            this.setErrorListItem("date_to", "The date to (" + date_to + ") is invalid. Use format: yyyy-mm-dd"); 
            process_method = false;
        }
        
        if (process_method == true && LocalDate.parse(date_from).isAfter(LocalDate.parse(date_to)))
        {
            this.setErrorListItem("date_from_greater_than_date_to", "The supplied date_from (" + date_from + ") is after the date to (" + date_to + ")"); 
            process_method = false;            
        }
        
        if(process_method == true && (!this.mortgage_all_sorted.containsKey(date_from) || !this.mortgage_all_sorted.containsKey(date_to)))
        {
            // First make sure any extremely dodgy long input is truncated....
            String date_from_to_string = date_from.substring(0, Math.min(11, date_from.length())) 
                    + " " + date_to.substring(0, Math.min(11, date_to.length()));

            this.setErrorListItem("date_from_or_to_not_exist","The range entered (" + date_from_to_string + ") was invalid, or dates do not exist in this run: (format: -r yyyy-mm-dd yyyy-mm-dd)\n");
            //msgs.setMessageString("The range entered (" + date_from_to_string + ") was invalid, or dates do not exist in this run: (correct format: yyyy-mm-dd yyyy-mm-dd)\n");
        }
        else if(process_method == true)
        {
            this.mortgage_all_sorted.subMap(date_from, date_to).forEach((key, value)->{
                String[] value_items = value.split(" ");
                this.setMortgageDayFiguresLine(key, value_items);            
            });            
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
        msgs.resetMessageString(); // clear any previous results
        if(this.mortgage_all_sorted.containsKey(date))
        {
            String[] value_items = this.mortgage_all_sorted.get(date).split(" ");
            
            this.setMortgageDayFiguresLine(date, value_items);
        }
        else
        {
            String err = "Could not find a record for the date, " + date + "."; 
            err += "The date must be between " + this.getCalendarDateFrom() ;
            err += " and " + this.getCalendarDateTo(); 
            this.setErrorListItem(date, err);
            //this.setErrorListItem(date, err, true); // reset error list first with the true parameter
        }

    }
    // * Validation | START //
    /**
     * Check if a number entered in the console, GUI field, command line parameter, etc is a number
     * @param number
     * @return 
     */
    public boolean checkIfInputNumberIsADouble(String number)
    {
        return Pattern.matches(DOUBLE_PATTERN, number);
    }
    /**
     * Int version overload
     * @param console_input
     * @param max_num
     * @param field_name
     * @param field_label
     * @return 
     */
    public boolean checkIfInputNumberTooLarge(String console_input, int max_num, String field_name, String field_label)
    {
        boolean too_large = Double.valueOf(console_input) > max_num;
        if(too_large)
        {
            this.setErrorListItem(field_name, "The value entered for '" +field_label+ "' is too large");
        }
        return too_large;
    }
    /**
     * double number version overload
     * @param console_input
     * @param max_num
     * @param field_name
     * @param field_label
     * @return 
     */
    public boolean checkIfInputNumberTooLarge(String console_input, double max_num, String field_name, String field_label)
    {
        boolean too_large = Double.valueOf(console_input) > max_num;
        if(too_large)
        {
            this.setErrorListItem(field_name, " * The value entered for '" + field_label + "' is too large: (Maximum: " + max_num + ").");
        }
        return too_large;
    }
    /**
     * 
     * @param console_input
     * @param min_num
     * @param field_name
     * @param field_label
     * @return 
     */
    public boolean checkIfInputNumberTooSmall(String console_input, double min_num, String field_name, String field_label)
    {
        boolean too_small = Double.valueOf(console_input) < min_num;
        if(too_small)
        {
            this.setErrorListItem(field_name, " * The value entered for '" + field_label + "' is too small (minimum: " + min_num + ").");
        }
        return too_small;
    }
    
    private boolean isDateFromGreaterThanDateTo()
    {
        boolean isdatefromgreaterthandateto = this.calendar_date_from.isAfter(this.calendar_date_to);
        
        if(isdatefromgreaterthandateto)
        {
            msgs.resetMessageString("Date from (" 
                + this.calendar_date_from.toString() + ") appears to be greater than Date to (" +  this.calendar_date_to.toString() + ")" ); // clear any previous results and set string            
        }

        //return true;
        return isdatefromgreaterthandateto;
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
    /**
     * 
     * @param number_string
     * @param parameter_name
     * @param message
     * @return 
     */
    public boolean validateNumberAsDouble(String parameter_name, String number_string, String message)
    {
        if(!this.checkIfInputNumberIsADouble(number_string))
        {
            this.setErrorListItem(parameter_name, message);
            return false;
        }
        return true;
    }
    /**
     * 
     * @param number_string
     * @param parameter_name
     * @return 
     */
    public boolean validateNumberAsDouble(String parameter_name, String number_string )
    {
        if(!this.checkIfInputNumberIsADouble(number_string))
        {
            this.setErrorListItem(parameter_name, "The number entered is not valid");
            return false;
        }
        return true;
    }
    /**
     * This overload works without attempting to reset the items in the error_list property.
     * @param control_name
     * @param error_message 
     */
    private void setErrorListItem(String control_name, String error_message)
    {
        this.setErrorListItem(control_name ,error_message, false);
    }
    /**
     * Overload version with the ability to reset the error list, if necessary.
     * @param control_name
     * @param error_message
     * @param reset_error_list 
     */
    private void setErrorListItem(String control_name, String error_message, boolean reset_error_list)
    {
        if(reset_error_list)
        {
            this.resetErrorList();
        }
        this.error_list.put(control_name ,error_message);
    }    
    public int getErrorListCount()
    {
        return this.error_list.size();
    }
    
    public HashMap getErrorListItems()
    {
        return this.error_list;
    }
    
    /**
     * Get error messages as a string delimited by new line and reset error list property before returning.
     * @return 
     */
    public String getErrorListMessages()
    {
        return this.getErrorListMessages("\n", true);
    }    
    /**
     * Get error messages as a string with the supplied delimiter.
     * @param delimiter
     * @param reset_error_list
     * @return 
     */
    public String getErrorListMessages(String delimiter, boolean reset_error_list)
    {
        this.error_list_messages = "";
        this.error_list.forEach((label,message)->
        {
           this.error_list_messages += message + delimiter;
        });
        String return_messages = this.error_list_messages;
        if(reset_error_list)
        {
            this.resetErrorList(); // Potentially causes problems if this is not done when re-running operation.
        }
        
        return return_messages;
    }    
    private void resetErrorList()
    {
        this.error_list.clear();
        //System.out.println("++ DEBUG ++:\nerror_list_clear_results:" + this.error_list.toString() + "\n+++");
    }
    
    public Object[] getErrorListKeysArray()
    {
        return this.error_list.keySet().toArray();
    }
    public Object[] getErrorListValuesArray()
    {
        return this.error_list.values().toArray();
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
    /**
     * A public alias of setCalendarDateFrom method for command line app when date is not entered
     */
    public void setDefaultDateFrom()
    {
        this.calendar_date_from = LocalDate.now();
    }
    
    /**
     * A public alias of setCalendarDateTo for command line app when date is not entered
     */
    public void setDefaultDateTo()
    {
        if(this.calendar_date_to == null)
        {
           msgs.resetMessageString("** DEBUG: calendar_date_to WAS NULL **\n");  // clear any previous results and set string
           this.calendar_date_to = LocalDate.now().plusMonths(DATE_PLUS_MONTHS);
        }
        
    }
    
    public String getCalendarDateFrom()
    {
        return this.calendar_date_from.toString();
    }
    
    
    public String getCalendarDateTo()
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
    private double getMortgageRemainingInitial()
    {
        return this.mortgage_remaining_initial;
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
