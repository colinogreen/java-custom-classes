package my.custom.finance;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

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
    private double total_overpayments_last_run = 0;

    final static int DATE_PLUS_MONTHS = 18;
    //private HashMap<String, String> mortgage_summary = new HashMap<>();
    //final private TreeMap<String, String> mortgage_summary_sorted = new TreeMap<>();
    final private TreeMap<String, String> mortgage_all_sorted = new TreeMap<>();

    final private TreeMap<String, String> mortgage_milestones = new TreeMap<>();
    final private TreeMap<String, Double> mortgage_overpayment = new TreeMap<>(); // Add overpayment date and amount
    
    private boolean milestone_int_less_one_per_day = false;
    private boolean milestone_25percent_amount_paid = false;
    private boolean milestone_50percent_amount_paid = false;
    private boolean milestone_75percent_amount_paid = false;
    
    public MortgageCalculator()
    {
        //this.addMortgageOverpayment();
    }
    
    /**
     * debugging version of method overload.
     */
    private void addMortgageOverpayment()
    {
        //mortgage_overpayment.put("2023-02-11", 499.0);
        mortgage_overpayment.put("2019-02-11", 499.0);
        //mortgage_overpayment.put("2026-03-22", 4400.50);
        mortgage_overpayment.put("2022-05-22", 4400.50);
    }

    /**
     * Regular version of method overload to add overpayment amounts by date.
     * @param date
     * @param amount 
     */
    public void addMortgageOverpayment(String date, double amount)
    {
        mortgage_overpayment.put(date, amount);
        //mortgage_overpayment.put(date, Double.valueOf(this.formatNumberToDecimalPlaces(2,amount)));
        msgs.resetMessageString("An overpayment of " + this.formatNumberToDecimalPlaces(2,amount) +" was set for the date, " + date + ".\nRe-run calculations with the -r parameter.\n");
    }
    
    public String listMortgageOverpayments()
    {
        String msg = "No overpayments found";
        msgs.resetMessageString();
        this.mortgage_overpayment.forEach((date, text)->{
            msgs.setMessageString("Date: " + date + " - Amount: " + this.formatNumberToDecimalPlaces(2,text));
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
//    protected double getMortgageRemainingInitial()
//    {
//        return this.mortgage_remaining_initial;
//    }  
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
            return false;// If too_large = true           
        }
        
        //boolean too_small = this.checkIfInputNumberTooSmall(amount, min_num, field_name, field_label);
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
        msg += "Mortgage interest rate: " + this.getInterestRate() + "\n";
        msg += "Date range: " + this.getCalendarDateFrom() + " - " + getCalendarDateTo() +"\n";
        msg += "Total interest paid: " + this.getInterestPayableTotal() +"\n";
        msg += "Total payable: " + this.getTotalMortgagePayableIncInterest() +"\n";
        //msg += "\n** Debug Daily interest total original: " + this.getDailyInterestTotalOriginal()+"\n";
        //msg += "** Debug Total payable original: " + this.getTotalPayableIncInterestOriginal()+"\n";
        //if(this.mortgage_overpayment.size() > 0)
        if(this.getTotalOverpaymentsLastRun() > 0)
        {
            msg += "\nNote: You could save " + this.formatNumberToDecimalPlaces(2,getDailyInterestSavedByOverpaying()) 
                    + " in interest charges\nas a result of your total overpayments of " + this.formatNumberToDecimalPlaces(2,this.getTotalOverpaymentsLastRun()) + ".\n";
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
            this.processMortgateInterestCalculation();            
        }
    }
    
    private void resetMortgageInterestVariables()
    {
        this.daily_interest_total = 0;
        
        milestone_int_less_one_per_day = false;
        milestone_25percent_amount_paid = false;
        milestone_50percent_amount_paid = false;
        milestone_75percent_amount_paid = false; 
        this.mortgage_milestones.clear();
        this.mortgage_all_sorted.clear();
    }
    
    public void setInitialMortgagePaymentAndInterestTotals()
    {
        this.setDailyInterestTotalOriginal(Double.valueOf(this.getInterestPayableTotal()));
        //this.setTotalPayableIncInterestOriginal(Double.valueOf(this.getInterestPayableTotal()));
        //System.out.println("** Debug MortgageCalculator.setInitialMortgagePaymentAndInterestTotals: this.getDailyInterestTotalOriginal(): "+ this.getDailyInterestTotalOriginal() + " **\n");
        this.total_payable_inc_interest_original = this.total_payable_inc_interest;
    }
    
    public void processMortgateInterestCalculation()
    {
        float dayCount = Duration.between(this.calendar_date_from.atStartOfDay(), this.calendar_date_to.atStartOfDay()).toDays();
        msgs.resetMessageString("** Calculations are based on a monthly repayment of £" + month_repayment + " **"); // clear any previous results and set string
        //this.mortgage_remaining_initial = this.mortgage_remaining;
        this.mortgage_remaining_increment = this.mortgage_remaining;
        LocalDate date = this.calendar_date_from;

        //LocalDate date_add = date.plusDays((int)dayCount);
        LocalDate date_add_single;
        
        // Set the total overpayments since the last run of the calculations (as in this run)
        this.setTotalOverpaymentsLastRun(this.getTotalOverpayments());
        
        for(int i = 0; i <= (int)dayCount; i++)
        {
            int day_type = 0; // 1= regular mortgage repayment day; 3= overpayment day; 4 = mortgage_repayment day + overpayment day, etc.
            date_add_single = date.plusDays(i);
            
                       
            // If the loop is not at the very first item and it is the first day of the month, reduce the mortgage remaining by the mortgage amount.
            if(i != 0 && date_add_single.getDayOfMonth() == 1)
            {
                day_type +=1; // Mortgage payment day
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
                this.mortgage_remaining_increment = (this.mortgage_remaining_increment - this.mortgage_overpayment.get(date_add_single.toString()));
                this.addMortgageMilestone(date_add_single.toString(), "An overpayment of " +  formatNumberToDecimalPlaces(2,this.mortgage_overpayment.get(date_add_single.toString()))
                        + " was made. Mortgage remaining is "+ formatNumberToDecimalPlaces(2,this.mortgage_remaining_increment ) + ".");
            }
            this.day_int_charge = (this.getDayInterestRate() * this.mortgage_remaining_increment / 100);
            String mort_entry_string = String.format("%.2f",this.mortgage_remaining_increment) + " " + this.interest_rate + " " + String.format("%.2f",this.day_int_charge);

            //if(i != 0 && date_add_single.getDayOfMonth() == 1)
            if(i != 0 && (day_type== 1 || day_type == 3 || day_type == 4))
            {    
                 // check that mortgage int day rate is below 1 or not and make a note for the milestones report
                this.checkMortMilestoneIntRateLessThanOnePerDay(date_add_single.toString());
                 // Check whether a certain percentage of the entered mortgage total has been paid back and make a note for milestones
                this.checkMortMilestonePercentAmountPaid(date_add_single.toString());

            }
            
            mort_entry_string += " " + day_type;
            
            mortgage_all_sorted.put(date_add_single.toString(),mort_entry_string);

            if(this.mortgage_remaining_increment <= 0)
            {
                this.addMortgageMilestone(date_add_single.toString(), "It is estimated that the mortgage balance will have been paid in full on this date.");
                break; // finish up, as the mortgage has been paid!
            }
            
            this.mortgage_remaining_increment += this.day_int_charge;
            this.addToDailyInterestTotal(this.day_int_charge);

        }
        //msgs.setMessageString("** Calculations were based on a monthly repayment of £" + month_repayment + " **","\n");

        //msgs.setMessageString("== Final amount of days ==","\n");
        //msgs.setMessageString("Date " + date + " plus " + (int)dayCount + " days is "+date_add,"\n"); 
        
        
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
            this.setErrorListItem("date_from_greater_than_date_to", "The supplied range start date (" + date_from + ") is after the end date (" + date_to + ")"); 
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
            String date_to_full = LocalDate.parse(date_to).plusDays(1).toString(); // * make sure to include final date in the range
            this.mortgage_all_sorted.subMap(date_from, date_to_full).forEach((key, value)->{
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
        this.mortgage_all_sorted.forEach((key, value)->{
            
            String[] value_items = value.split(" ");
            // Check if mortgage repayment day by querying the fourth array key (should it exist)
            if(value_items.length == 4 && (value_items[3].equals("1") || value_items[3].equals("4")))
            {
                this.setMortgageDayFiguresLine(key, value_items);
            }       

        });                       
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

        //double overpayment_limit = this.getTotalOverpayments() + this.getMortgageRemainingForDate(date);
        double overpayment_limit = this.getMortgageRemainingForDate(date) - this.getTotalOverpayments();
        //System.out.println("-- Debugging method (MortgageCalculator.isMortgageOverpaymentAmountForDayValid): overpayment_limit = " + overpayment_limit + " --\n");
        if(amount > overpayment_limit)
        {
            this.setErrorListItem("overpayment_err", "The overpayment amount cannot be higher than the remaining mortgage (" 
                    + this.getMortgageRemainingForDate(date) + ") for the given date minus existing overpayments (" + this.getTotalOverpayments()+ ")");
            return false;
        }
        return true;
    }
    
    private void setTotalOverpaymentsLastRun(double total)
    {
        this.total_overpayments_last_run = total;
    }
    
    private double getTotalOverpaymentsLastRun()
    {
        return this.total_overpayments_last_run;
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
        if(this.mortgage_all_sorted.containsKey(date))
        {
            return Double.valueOf(this.mortgage_all_sorted.get(date).split(" ")[0]);
        }
        return 0.00;
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
            String err = "Could not find a record for the date, " + date + ". "; 
            err += "The date must be between " + this.getCalendarDateFrom() ;
            err += " and " + this.getCalendarDateTo() + " but it could be that the final mortgage payment date may be earlier."; 
            this.setErrorListItem(date, err);
            //this.setErrorListItem(date, err, true); // reset error list first with the true parameter
        }

    }
}