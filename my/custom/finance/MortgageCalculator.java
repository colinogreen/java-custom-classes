package my.custom.finance;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

import my.custom.finance.MortgagePaymentDays;
import my.custom.finance.MortgagePaymentDay;

/**
 * 
 * @author Colin M.
 */
public class MortgageCalculator extends FinanceApr
{
    //protected double mortgage_remaining_initial;
    protected double mortgage_remaining_increment;
    protected double mortgage_remaining;
    
    final public double MAX_MORTGAGE_INT_RATE = 18.0; // Based on highest UK base rate ever: 17%
    final public int MAX_MORTGAGE_TERM = 40; // In years. Recent UK max
    final public int MAX_MORTGAGE_LOAN= 500000; // Recent UK max loan: around 411,000
    final public int MAX_MONTHLY_REPAYMENT = 3000; // Hope anyone should ever have to repay higher than that!  
    final public double MIN_OVERPAYMENT = 10; 
    private double total_overpayments;
    private double total_overpayments_this_run = 0;

    final static int DATE_PLUS_MONTHS = 18;

    final private TreeMap<String, String> mortgage_all_sorted = new TreeMap<>();

    final private TreeMap<String, String> mortgage_milestones = new TreeMap<>();
    final private TreeMap<String, Double> mortgage_overpayment = new TreeMap<>(); // Add overpayment date and amount
    
    private boolean milestone_int_less_one_per_day = false;
    private boolean milestone_25percent_amount_paid = false;
    private boolean milestone_50percent_amount_paid = false;
    private boolean milestone_75percent_amount_paid = false;
    
    final private MortgagePaymentDays mortgage_payment_days = new MortgagePaymentDays();
    
    public MortgageCalculator()
    {
        //this.addMortgageOverpayment();
    }

    private void addMortgagePaymentDay(String date, MortgagePaymentDay day)
    {
        //System.out.println("-- Debug method, addMortgagePaymentDay: date - " + date + "\n");
        this.mortgage_payment_days.addDay(date, day);
    }
    
    private void clearMortgagePaymentDays()
    {
        this.mortgage_payment_days.clearRecords();
    }
    
    private MortgagePaymentDays getMortgagePaymentDays()
    {
        return this.mortgage_payment_days;
    }
    
    /**
     * @deprecated
     * debugging version of method overload.
     */
    private void addMortgageOverpayment()
    {
        mortgage_overpayment.put("2019-02-11", 499.0);
        mortgage_overpayment.put("2022-05-22", 4400.50);
    }

    /**
     * Regular version of method overload to add overpayment amounts by date.
     * @param date
     * @param amount 
     */
    public void addMortgageOverpayment(String date, double amount)
    {
        this.mortgage_overpayment.put(date, amount);
        //mortgage_overpayment.put(date, Double.valueOf(this.formatNumberToDecimalPlaces(2,amount)));
        msgs.resetMessageString("An overpayment of " + this.formatNumberToDecimalPlaces(amount) +" was set for the date, " + date + ".\nRe-run calculations with the -r parameter.\n");
    }
    
    public String listMortgageOverpayments()
    {
        String msg = "No overpayments found";
        msgs.resetMessageString();
        this.mortgage_overpayment.forEach((date, text)->{
            msgs.setMessageString("Date: " + date + " - Amount: " + this.formatNumberToDecimalPlaces(text));
        });
        
        if(msgs.getMessageString()!= null )
        {
            msg = msgs.getMessageString();
        }
        return msg;
    }
    
    public boolean overpaymentInputValidateAndProcess(String date, String amount)
    {
        if(!this.isLocalDateValid(date))
        {
            this.setErrorListItem("overpay_date", "The date (" + this.truncateLongString(date) + ") appears to be invalid");
            //return false;
        }
        else if(!this.getMortgagePaymentDays().isMortgagePaymentDayExists(date) )
        {
            this.setErrorListItem("overpay_date_not_exist", "The date (" + this.truncateLongString(date) 
                    + ") does not exist in the current range of records. Valid range(" + this.getMortgagePaymentDays().getValidDateRange() + ")");
        }
        if(!this.checkIfInputNumberIsADouble(amount))
        {
            this.setErrorListItem("overpay_amount", "The overpayment amount (" + this.truncateLongString(amount) + ") appears to be invalid");
        }
     
        if(this.getErrorListCount()>0)
        {
            return false;
        }
        // This will check the validity/size of the overpayment amount and set error messages (if necessary) based on a valid date and overpayment amount being entered
        return this.isMortgageOverpaymentAmountForDayValid(date, Double.valueOf(amount));
    }
    
    public boolean isMortgageOverpaymentEntryExists(String date)
    {
        return this.mortgage_overpayment.containsKey(date);
    }
            
    public void removeMortgageOverpaymentEntry(String date)
    {
        this.mortgage_overpayment.remove(date);
    }
    
    /**
     * Used in original pre-2021 mortgage-calculator
     * @return 
     */
    protected double getMortgageRemaining()
    {
        return this.mortgage_remaining;
    }
 
    protected double getMortgageRemainingIncrement()
    {
        return this.mortgage_remaining_increment;
    }
        
    protected String getTotalMortgagePayableIncInterest()
    {
        return this.formatNumberToDecimalPlaces(2,(this.getMortgageRemaining() + Double.valueOf(this.getInterestPayableTotal())));
    }
    
    protected String getTotalMortgagePayableSavedByOverpaying()
    {
        return this.formatNumberToDecimalPlaces(2,(this.getTotalPayableIncInterestOriginal() - Double.valueOf(this.getTotalMortgagePayableIncInterest())));
    }    
    public boolean setMonthlyRepaymentAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        if (this.inputValueValidated(amount, max_num, min_num, field_name, field_label))
        {
            this.setMonthRepayment(Double.valueOf(amount));
            return true;
        }
        
        return false;
    }
    
    public boolean setMonthlyInterestAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
        if (this.inputValueValidated(amount, max_num, min_num, field_name, field_label))
        {
            //this.setInterestRate(Double.valueOf(amount));
            this.setInterestRate(Float.parseFloat(amount));
            return true;
        }
        
        return false;
    }
    
    
    /**
     * Used in original pre-2021 mortgage-calculator
     * @param amount 
     */
    public void setMortgageRemaining(double amount)
    {
        this.mortgage_remaining = amount;
    }
    
    public boolean setMortgageRemainingAmount(String amount, double max_num, double min_num, String field_name, String field_label)
    {
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
            return false;// If too_large = true           
        }
        
        if(this.checkIfInputNumberTooSmall(amount, min_num, field_name, field_label))
        {            
            return false; // If too_small = true
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
           msgs.setMessageString("start date is set to " +  this.calendar_date_from.toString() +"\n");  // clear any previous results and set string
        }
        else
        {

            this.calendar_date_to = this.calendar_date_from.plusMonths(DATE_PLUS_MONTHS);
            msgs.setMessageString("Setting the end date, " + this.calendar_date_to.toString() + " based on the start date, " 
                    + this.calendar_date_from.toString() + " plus " + MortgageCalculator.DATE_PLUS_MONTHS + " months.\n");  // clear any previous results and set string
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
        msg += "Initial Mortgage payment remaining: " + this.getMortgageRemaining() + "\n";
        msg += "Repayment amount: " + this.getMonthRepayment() + "\n";
        msg += "Mortgage interest rate: " + this.formatNumberToDecimalPlaces(this.getInterestRate()) + "\n";
        msg += "Date range: " + this.getCalendarDateFrom() + " - " + getCalendarDateTo() +"\n";
        msg += "Total interest paid: " + this.getInterestPayableTotal() +"\n";
        msg += "Total payable: " + this.getTotalMortgagePayableIncInterest() +"\n";

        if(this.getExistingOverpayments() > 0)
        {
            msg += "\nNote: You could save " + this.formatNumberToDecimalPlaces(2,getDailyInterestSavedByOverpaying()) 
                    + " in interest charges\nas a result of your total overpayments of " + this.formatNumberToDecimalPlaces(2,this.getExistingOverpayments()) + ".\n";
        }
        msg += "----------------------------------------------------------------\n";
        
        return msg;
    }

    /**
     * Overload that resets the mortgage calc variables before re-running the calculation.
     * @param reset_variables 
     */
    public void processMortgateInterestCalculation(boolean reset_variables)
    {
        if(reset_variables)
        {
            this.resetMortgageInterestVariables();
            this.clearMortgagePaymentDays();
            this.processMortgateInterestCalculation();            
        }
    }
    
    private void resetMortgageInterestVariables()
    {
        this.daily_interest_total = 0;
        
        this.milestone_int_less_one_per_day = false;
        this.milestone_25percent_amount_paid = false;
        this.milestone_50percent_amount_paid = false;
        this.milestone_75percent_amount_paid = false; 
        this.mortgage_milestones.clear();
        this.mortgage_all_sorted.clear();
    }
    
    public void setInitialMortgagePaymentAndInterestTotals()
    {
        this.setDailyInterestTotalOriginal(Double.valueOf(this.getInterestPayableTotal()));
        this.total_payable_inc_interest_original = this.total_payable_inc_interest;
    }
    
    public void processMortgateInterestCalculation()
    {
        float dayCount = Duration.between(this.calendar_date_from.atStartOfDay(), this.calendar_date_to.atStartOfDay()).toDays();
        msgs.resetMessageString("** Calculations are based on a monthly repayment of ??" + month_repayment + " **"); // clear any previous results and set string
        this.mortgage_remaining_increment = this.mortgage_remaining;
                
        LocalDate date = this.calendar_date_from;
        LocalDate date_add_single;
        
        // Set the total overpayments for the current run of calculations.
        this.setExistingOverpayments(this.getTotalOverpayments());

        for(int i = 0; i <= (int)dayCount; i++)
        {
            MortgagePaymentDay mpd = new MortgagePaymentDay();
            int day_type = 0; // 1= regular mortgage repayment day; 3= overpayment day; 4 = mortgage_repayment day + overpayment day, etc.
            date_add_single = date.plusDays(i);
                                  
            // If the loop is not at the very first item and it is the first day of the month, reduce the mortgage remaining by the mortgage amount.
            if(i != 0 && date_add_single.getDayOfMonth() == 1)
            {
                day_type +=1; // Mortgage payment day
                mpd.setMortgageRepaymentDay();// Mortgage payment day
                if(mortgage_remaining_increment > this.month_repayment)
                {
                    this.mortgage_remaining_increment -= this.month_repayment; // deduct monthly mortgage repayment if it is the 1st of a month and not the first run of the loop (which may take into account first day, anyway.                    
                }
                else
                {
                    this.mortgage_remaining_increment = (this.mortgage_remaining_increment - this.mortgage_remaining_increment); // Possibly the final mortgage payment, so, finish up!                    
                }
            }
            
            if(this.mortgage_overpayment.containsKey(date_add_single.toString()))
            {
                day_type +=3; // Register Mortgage overpayment day
                mpd.setOverpaymentDay();// Register Mortgage overpayment day
                this.mortgage_remaining_increment = (this.mortgage_remaining_increment - this.mortgage_overpayment.get(date_add_single.toString()));
                this.addMortgageMilestone(date_add_single.toString(), "An overpayment of " +  formatNumberToDecimalPlaces(2,this.mortgage_overpayment.get(date_add_single.toString()))
                        + " was made. Mortgage remaining is "+ formatNumberToDecimalPlaces(2,this.mortgage_remaining_increment ) + ".");

            }
            this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining_increment / 100);

            mpd.setData(date_add_single.toString(),this.mortgage_remaining_increment,this.interest_rate , this.day_int_charge );
            
            if(i != 0 && (day_type== 1 || day_type == 3 || day_type == 4))
            {    
                // check that mortgage int day rate is below 1 or not and make a note for the milestones report
                this.checkMortMilestoneIntRateLessThanOnePerDay(date_add_single.toString());
                // Check whether a certain percentage of the entered mortgage total has been paid back and make a note for milestones
                this.checkMortMilestonePercentAmountPaid(date_add_single.toString());
            }
            // Add the data from the MortgagePaymentDay class to the wrapper MortgagePaymentDays class
            this.addMortgagePaymentDay(date_add_single.toString(), mpd);

            // Stop processing if mortgage remaining is less than 1
            if(this.mortgage_remaining_increment < 1)
            {
                this.addMortgageMilestone(date_add_single.toString(), "It is estimated that the mortgage balance will have been paid in full on this date.");
                break; // finish up, as the mortgage has been paid!
            }
                        
            this.mortgage_remaining_increment += this.day_int_charge;
            this.addToDailyInterestTotal(this.day_int_charge);

        }        
    }
    
    private void checkMortMilestoneIntRateLessThanOnePerDay(String date)
    {
        Float int_charge = Float.valueOf(String.format("%.2f",this.day_int_charge));
        if(this.milestone_int_less_one_per_day == false && (int_charge < 1))
        {
            this.addMortgageMilestone(date, "The daily interest rate would go below 1 for the first time and would be " + int_charge
                    + " (with " + Float.valueOf(String.format("%.2f",this.mortgage_remaining_increment)) + " remaining on the mortgage total supplied).");
            this.milestone_int_less_one_per_day= true; // set true so that this is no longer activated.
        }
    }
    
    private void checkMortMilestonePercentAmountPaid(String date)
    {

        if(this.milestone_25percent_amount_paid == false )
        {
            
            double mortgage_percent_less_paid = this.mortgage_remaining - (this.mortgage_remaining * 25 / 100 );
            if( this.mortgage_remaining_increment < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining_increment));
                this.addMortgageMilestone(date, "The mortgage remaining is now at least 25 percent less than the initial amount (" + mort_remain + ").");
                this.milestone_25percent_amount_paid = true;               
            }

        }
        
        else if(this.milestone_50percent_amount_paid == false )
        {
            double mortgage_percent_less_paid = this.mortgage_remaining - (this.mortgage_remaining * 50 / 100 );
            if( this.mortgage_remaining_increment < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining_increment));
                this.addMortgageMilestone(date, "The mortgage figure remaining is now at least 50 percent less than the initial amount (" + mort_remain + ").");
                this.milestone_50percent_amount_paid = true;                
            }            

        }
        
        else if(this.milestone_75percent_amount_paid == false )
        {
            double mortgage_percent_less_paid = this.mortgage_remaining - (this.mortgage_remaining * 75 / 100 );
            if( this.mortgage_remaining_increment < mortgage_percent_less_paid)
            {
                Float mort_remain = Float.valueOf(String.format("%.2f",this.mortgage_remaining_increment));
                this.addMortgageMilestone(date, "The mortgage amount remaining is now at least 75 percent less than the initial amount (" + mort_remain + ").");
                this.milestone_75percent_amount_paid = true;                
            }            

        }
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
        
        return "There are no mortgage milestones records." + delimiter;
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

        boolean process_method = true;
        this.resetErrorList();
        if(!this.isDateEnteredValid(date_from))
        {
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
            this.setErrorListItem("date_from_greater_than_date_to", "The supplied range start date (" + date_from + ") is after the end date (" + date_to + ")"); 
            process_method = false;            
        }
        
        if(process_method == true && (!this.getMortgagePaymentDays().isMortgagePaymentDayExists(date_from) 
                || !this.getMortgagePaymentDays().isMortgagePaymentDayExists(date_to)))
        {
            // First make sure any extremely dodgy long input is truncated....
            String date_from_to_string = date_from.substring(0, Math.min(11, date_from.length())) 
                    + " " + date_to.substring(0, Math.min(11, date_to.length()));

            this.setErrorListItem("date_from_or_to_not_exist","The range entered (" + date_from_to_string 
                    + ") was invalid, or dates do not exist in this run: (format: -rn yyyy-mm-dd yyyy-mm-dd)\n");
        }
        else if(process_method == true)
        {
            // Clear and populate the message string with the results of the range search.
            msgs.resetMessageString(this.getMortgagePaymentDays().getMortgagePaymentDataForRange(date_from, date_to));          
        }

               
    }
    private void setMortgageDayFiguresAllEntries()
    {
        msgs.resetMessageString(this.getMortgagePaymentDays().getMortgagePaymentDayData(false, "\n"));      
    }
    /**
     * Show each entry for summary
     */
    private void setMortgageDayFiguresSummary()
    {
        msgs.resetMessageString(this.getMortgagePaymentDays().getMortgagePaymentDayData(true, "\n"));                    
    }
    /**
     * 
     * @param date
     * @param amount
     * @return true or false
     */
    private boolean isMortgageOverpaymentAmountForDayValid(String date, double amount)
    {
        if(amount < this.MIN_OVERPAYMENT)
        {
            this.setErrorListItem("min_overpayment_err", "The overpayment amount entered is smaller than the minimum overpayment allowed (" + this.MIN_OVERPAYMENT + ")");
            return false;
        }

        double overpayment_limit = Double.valueOf(this.formatNumberToDecimalPlaces(this.getMortgageRemainingForDate(date) - this.getTotalOverpaymentsUnprocessed()));
        if(amount > overpayment_limit)
        {
            this.setErrorListItem("overpayment_err", "The overpayment amount cannot be higher than the remaining mortgage (" 
                    + this.formatNumberToDecimalPlaces(this.getMortgageRemainingForDate(date)) + ") for the given date minus existing unprocessed overpayments (" + this.formatNumberToDecimalPlaces(this.getTotalOverpaymentsUnprocessed()) + ")");
            return false;
        }
        return true;
    }
    
    private void setExistingOverpayments(double total)
    {
        this.total_overpayments_this_run = total;
    }
    
    private double getExistingOverpayments()
    {
        return this.total_overpayments_this_run;
    }
    /**
     * try to calculate unprocessed overpayments for working out if adding overpayment for given date is allowed or not
     * @return overpayments_unprocessed or zero
     */
    private double getTotalOverpaymentsUnprocessed()
    {
        double overpayments_unprocessed = (this.getTotalOverpayments() -getExistingOverpayments());
        
        if(overpayments_unprocessed > 0)
        {
            return overpayments_unprocessed;
        }
        
        return 0.00;
    }
    /**
     * 
     * @return total_overpayments (entered so far)
     */
    private double getTotalOverpayments()
    {
        this.total_overpayments = 0;
        
        this.mortgage_overpayment.forEach((key, value)->{
            this.total_overpayments += value;
        });
       
        return this.total_overpayments;
    }
    /**
     * 
     * @param date
     * @return mortgage remaining or 0.00
     */
    private Double getMortgageRemainingForDate(String date)
    {
        //if(this.mortgage_all_sorted.containsKey(date))
        if(this.getMortgagePaymentDays().isMortgagePaymentDayExists(date))
        {
            return this.getMortgagePaymentDays().getDay(date).getMortgageRemaining();            
        }

        return 0.00;
    }
    /**
     * @deprecated
     * @param key
     * @param value_items 
     */
    private void setMortgageDayFiguresLine(String key,String[] value_items)
    {
        msgs.setMessageString("Date: " + key);  // Start creating output
        msgs.setMessageString("Mortgage Remaining: " + value_items[0]," ");
        msgs.setMessageString("Mortgage Rate: " + value_items[1]," ");
        msgs.setMessageString("Interest per day: " + value_items[2]," ");        
    }
    /**
     * 
     * @param date 
     */
    public void setMortgageIndividualDateRecord(String date)
    {
        if(this.getMortgagePaymentDays().isMortgagePaymentDayExists(date))
        {
            String record = this.getMortgagePaymentDays().getMortgagePaymentDayIndividualData(date, "\n");
            msgs.resetMessageString(record);
        }
        else
        {
            String err = "Could not find a record for the date, " + date + ". "; 
            err += "The date must be between " + this.getCalendarDateFrom() ;
            err += " and " + this.getCalendarDateTo() + " but it could be that the final mortgage payment date may be earlier."; 
            this.setErrorListItem(date, err);
        }

    }
}