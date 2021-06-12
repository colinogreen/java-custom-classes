package my.custom.finance;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 
 * @author colino20_04
 */
public class Mortgage_calc extends Finance_apr
{
    //* For validation

    final public double MAX_MORTGAGE_INT_RATE = 18.0; // Based on highest UK base rate ever: 17%
    final public int MAX_MORTGAGE_TERM = 40; // Recent UK max
    final public int MAX_MORTGAGE_LOAN= 500000; // Recent UK max loan: around 411,000
    final public int MAX_MONTHLY_REPAYMENT = 3000; // Hope should never go higher than that    

    final static int DATE_PLUS_MONTHS = 6;
    //private HashMap<String, String> mortgage_summary = new HashMap<>();
    final private TreeMap<String, String> mortgage_all_sorted = new TreeMap<>();
    final private TreeMap<String, String> mortgage_summary_sorted = new TreeMap<>();
    final private TreeMap<String, String> mortgage_milestones = new TreeMap<>();
    
    private boolean milestone_int_less_one_per_day = false;
    private boolean milestone_25percent_amount_paid = false;
    private boolean milestone_50percent_amount_paid = false;
    private boolean milestone_75percent_amount_paid = false;
    
    public Mortgage_calc()
    {
        //super();
        //this.msgs = new MessageDisplayer();
    }
    
    public boolean setMonthlyRepaymentAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        //if(!this.isNumberInputValid(apr, monthly_repayment,Double.valueOf(apr.MAX_MONTHLY_REPAYMENT), 10, "monthly_repayment", "monthly repayment"))
        if (this.inputValueValidated(amount, max_num, min_num, field_name, field_label))
        {
            this.setMonthRepayment(Double.valueOf(amount));
            return true;
        }
        
        return false;
    }
    
    public boolean setMonthlyInterestAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        //if(!this.isNumberInputValid(apr, monthly_repayment,Double.valueOf(apr.MAX_MONTHLY_REPAYMENT), 10, "monthly_repayment", "monthly repayment"))
        if (this.inputValueValidated(amount, max_num, min_num, field_name, field_label))
        {
            this.setInterestRate(Double.valueOf(amount));
            return true;
        }
        
        return false;
    }
    
    public boolean setMortgageRemainingAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        //if(!this.isNumberInputValid(apr, monthly_repayment,Double.valueOf(apr.MAX_MONTHLY_REPAYMENT), 10, "monthly_repayment", "monthly repayment"))
        if (this.inputValueValidated(amount, max_num, min_num, field_name, field_label))
        {
            this.setMortgageRemaining(Double.valueOf(amount));
            return true;
        }
        
        return false;
    }
    
    private boolean inputValueValidated(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        if(!this.checkIfInputNumberIsADouble(amount))
        {
            this.setErrorListItem(field_name, "The " + field_label + " appears not to be valid.");
            return false;
        }
        
        boolean too_large = this.checkIfInputNumberTooLarge(amount, max_num, field_name, field_label);
        if(too_large)
        {
            //System.out.println(this.getErrorListMessages());
            return false;           
        }
        boolean too_small = this.checkIfInputNumberTooSmall(amount, min_num, field_name, field_label);
        if(too_small)
        {
            //System.out.println(this.getErrorListMessages());
            return false;
        }  
        
        return true;            
    }
    /**
     * Overrides parent class
     */
    public void setDefaultDateTo()
    {
        if(this.calendar_date_from == null)
        {

           this.calendar_date_to = LocalDate.now().plusMonths(DATE_PLUS_MONTHS);
           msgs.setMessageString(" * date to is set to " +  this.calendar_date_from.toString() +"**\n");  // clear any previous results and set string
        }
        else
        {

            this.calendar_date_to = this.calendar_date_from.plusMonths(DATE_PLUS_MONTHS);
            msgs.setMessageString(" * Setting date to: " + this.calendar_date_to.toString() + " - based on date from: " 
                    + this.calendar_date_from.toString() + "**\n");  // clear any previous results and set string
        }
        
    }
    
            
    public void setMonthRepayment(double amount)
    {
        this.month_repayment = amount;
    }
    
    public double getMonthRepayment()
    {
        return this.month_repayment;
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
}