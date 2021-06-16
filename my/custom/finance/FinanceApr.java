/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom.finance;

//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
//import java.time.*;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.time.format.DateTimeParseException;

import my.custom.MessageDisplayer; // 2021-06-07 - New separate class to help display messages to console|GUI|Android app, etc.

/**
 *
 * @author Colin M.
 * This class uses the class MessageDisplayer at https://github.com/colinogreen/java-custom-classes/blob/master/my/custom/MessageDisplayer.java
 */
abstract class FinanceApr 
{  
    
    protected double interest_rate;

    
    //* For validation
    final protected static String DOUBLE_PATTERN = "[0-9]+(\\.){0,1}[0-9]*";
    final protected static String INTEGER_PATTERN = "\\d+";
    
    double day_interest_rate;
    //double day_interest_charge;
    protected double month_repayment;
    protected double day_int_charge;
    protected double daily_interest_total = 0;
    protected double total_payable_inc_interest = 0;
    
    protected String date_from;
    protected String date_to;
    
    protected LocalDate calendar_date_from;
    //private Calendar calendar_date_from;
    protected LocalDate calendar_date_to;
    //private Calendar calendar_date_to;

    protected String error_list_messages;
    final protected MessageDisplayer msgs;
    
    final protected HashMap<String, String> error_list = new HashMap<>();

    
    //public Finance_apr(double month_repayment, double mort_remain, double apr_int_rate)
    public FinanceApr()
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
    
    protected void addToDailyInterestTotal(double value)
    {
        this.daily_interest_total += value;
    }

    
    protected String getInterestPayableTotal()
    {
        return this.formatNumberToDecimalPlaces(2, this.daily_interest_total);
    }
    
    protected String formatNumberToDecimalPlaces(double number)
    {
        DecimalFormat d = new DecimalFormat("#.##");
        //String string_places = "%." + decimal_places + "f";
        return d.format(4);
    }
    
    protected String formatNumberToDecimalPlaces(int decimal_places,double number)
    {
        //DecimalFormat d = new DecimalFormat("#.##");
        String string_places = "%." + decimal_places + "f";
        
        return String.format(string_places, number);
    }
    /**
     * Truncate potentially long string from console input
     * @param string
     * @return 
     */
    protected String truncateLongString(String string)
    {
        return string.substring(0, Math.min(14, string.length()));
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
            this.setErrorListItem(field_name, "The value entered for '" + field_label + "' is too large: (Maximum: " + max_num + ").");
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
            //System.out.println("** DEBUG * The value entered for '" + field_label + "' is too small? (minimum: " + min_num + ") - input: " + console_input + " too_small?: " + too_small + ".");
        }

        return too_small;
    }
    
    public boolean isDateDifferenceGreaterThanLimit(String date_min, String date_max, String field_name_max, int years_limit)
    {
        LocalDate date_years_limit = LocalDate.parse(date_min).plusYears(years_limit);
        boolean is_greater_than_limit = LocalDate.parse(date_max).isAfter(date_years_limit);
        if(is_greater_than_limit)
        {
            this.setErrorListItem(field_name_max, " * The date entered '(" + date_max + ")' is too far after the date, '"+ date_min +"' (maximum: " + years_limit + ").");
        }
        return is_greater_than_limit;
    }
    
    protected boolean isDateFromGreaterThanDateTo()
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
//        msgs.resetMessageString("++ Debug Method: Finance_apr::isDateToGreaterThanDateFrom. Start date: " 
//                + this.calendar_date_to.toString() + " | End date: " +  this.calendar_date_from.toString() ); // clear any previous results and set string
        //return true;
        return this.calendar_date_to.isAfter(this.calendar_date_from);
    }
    /**
     * Do not use for LocalDate object. See isLocalDateValid method
     * @param date
     * @return 
     */
    public boolean isDateEnteredValid(String date)
    {
        SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
        sdfrmt.setLenient(false);
        /* Create Date object
         * parse the string into date 
         */
        try
        {
            sdfrmt.parse(date);
        }
        /* Date format is invalid */
        catch (ParseException e)
        {
            msgs.setMessageString("The date supplied (" + date + ") is invalid");
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
    
    public void resetMessageString()
    {
        msgs.resetMessageString();
    }
    /**
     * This overload works without attempting to reset the items in the error_list property.
     * @param control_name
     * @param error_message 
     */
    protected void setErrorListItem(String control_name, String error_message)
    {
        this.setErrorListItem(control_name ,error_message, false);
    }
    /**
     * Overload version with the ability to reset the error list, if necessary.
     * @param control_name
     * @param error_message
     * @param reset_error_list 
     */
    protected void setErrorListItem(String control_name, String error_message, boolean reset_error_list)
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

    
    public void setErrorListMessage(String control_name, String message)
    {
        this.setErrorListItem(control_name, message);
    }
    
    
    /**
     * Get error messages as a string delimited by new line.
     * @param reset_error_list
     * @return delimited string (of error messages)
     */
    public String getErrorListMessages(boolean reset_error_list)
    {
        return this.getErrorListMessages("\n", reset_error_list);
    }
    
    /**
     * Get error messages as a string delimited by new line. DO NOT reset error list before returning.
     * @return delimited string (of error messages)
     */
    public String getErrorListMessages()
    {
        return this.getErrorListMessages("\n", false);
    }    
    /**
     * Get error messages as a string with the supplied delimiter. reset error list with second parameter, if necessary.
     * @param delimiter
     * @param reset_error_list
     * @return delimited string (of error messages)
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
    
    protected void resetErrorList()
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
    
    /**
     * 
     * @param label
     * @param date_from_string
     * @return 
     */
    public boolean isLocalDateValid(String date_from_string)
    {
        try
        {
            LocalDate.parse(date_from_string);
            return true;
        }
        catch(DateTimeParseException e)
        {
            return false;
        }
        
    }
    /**
     * Overload that includes the ability to set an error list item.
     * @param label
     * @param date_from_string
     * @param set_error_list_item
     * @return 
     */
    public boolean isLocalDateValid(String label,String date_from_string, boolean set_error_list_item)
    {
        try
        {
            LocalDate.parse(date_from_string);
            return true;
        }
        catch(DateTimeParseException e)
        {
            if(set_error_list_item)
            {
                this.setErrorListItem(label, "The " + label + " supplied (" + date_from_string + ") is invalid");
            }
            
            return false;
        }
        
    } 
    protected boolean setCalendarDateFrom(String date_from_string)
    {
        try
        {
            this.calendar_date_from = LocalDate.parse(date_from_string);
            return true;
        }
        catch(DateTimeParseException e)
        {
            this.setErrorListItem("set_calendar_date_error", "The date supplied (" + date_from_string + ") is invalid");
            return false;
        }
        
    }

    protected boolean setCalendarDateTo(String date_to_string)
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
//    /**
//     * @deprecated
//     */
//    private void setDateRanges()
//    {
//        String date_set1[] = this.date_from.split("-");
//        //System.out.println(date_set[0] + "," + (Integer.valueOf(date_set[1]) -1) + "," + date_set[0]);
//        if(date_set1.length == 3)
//        {         
//            this.calendar_date_from = LocalDate.parse(date_from);            
//        }
//        else
//        {
//            this.setDefaultDateFrom();
//            msgs.resetMessageString("Note: default start date to: " + this.calendar_date_from.toString());  // clear any previous results and set string
//        }
//
//        String date_set2[] = this.date_to.split("-");
//        if(date_set1.length == 3)
//        {
////            int year = Integer.valueOf(date_set2[0]);
////            int month200 = (Integer.valueOf(date_set2[1]) -1);
////            int day = Integer.valueOf(date_set2[2]);
//            //this.calendar_date_to.of(year, month, day);            
//            this.calendar_date_to = LocalDate.parse(date_to);            
//        }
//        else
//        {
//            this.setDefaultDateTo();
//            msgs.resetMessageString("Note: Setting default end date to: " + this.calendar_date_to.toString() + "."); // clear any previous results and set string
//        }
//    }
    /**
     * Set a LocalDate based start date
     * A public alias of setCalendarDateFrom method for command line app when date is not entered
     */
    public void setDefaultDateFrom()
    {
        
        this.calendar_date_from = LocalDate.now();
        msgs.setMessageString("The date from is set to today's date: " +  this.calendar_date_from.toString() +".\n");
    }
    
    /**
     * NOTE: Overridden in child class, MortgageCalculator
     * Set a LocalDate based end date
     * A public alias of setCalendarDateTo for command line app when date is not entered
     */
    public void setDefaultDateTo()
    {
        if(this.calendar_date_from == null)
        {

           this.calendar_date_to = LocalDate.now().plusMonths(6);
           msgs.setMessageString(" * date to is set to " +  this.calendar_date_from.toString() +"**\n");  // clear any previous results and set string
        }
        else
        {

            this.calendar_date_to = this.calendar_date_from.plusMonths(6);
            msgs.setMessageString(" * Setting date to: " + this.calendar_date_to.toString() + " - based on date from: " 
                    + this.calendar_date_from.toString() + "**\n");  // clear any previous results and set string
        }
        
    }
    
    public String getCalendarDateFrom()
    {
        if(this.calendar_date_from != null)
        {
            return this.calendar_date_from.toString();
        }
        
        return ""; // Or return empty string, avoiding null pointer error.
        
    }
    
    
    public String getCalendarDateTo()
    {
        return this.calendar_date_to.toString();
    }
    /**
     * Note: This gets set by call to setInterestRate method
     */
    protected void setDayInterestRate()
    {
        this.day_interest_rate = (this.interest_rate / 365);
    }
    
    protected double getDayInterestRate()
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
        //System.out.println("The mortgage remaining has been set to been set to " + getMortgageRemaining());
        System.out.println("The interest rate has been set to " + getInterestRate());
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
    protected double getInterestRate()
    {
        return this.interest_rate;
    }
}
