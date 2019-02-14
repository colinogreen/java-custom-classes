/*
 * Created by Colin Morris on 01-Feb-2019.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom;

import java.util.ArrayList;

/**
 *
 * @author colino
 */

public class Savings_interest_engine {
// Used by both the Desktop app and the Android App!    
    private ArrayList<Double> periodInterest = new ArrayList<>();
    private ArrayList<Double> periodInterestSum = new ArrayList<>();
    //private double[] periodInterestSum = {};
    private ArrayList<Double> periodMonthlyInterest = new ArrayList<>();

    private ArrayList<Double> periodBalancePlusInterest = new ArrayList<>();
    // Monthly period calculations variables
    protected double lastPeriodAmount = 0.0;
    private int yearsInMonthsPeriod;
    private int yearsInMonthsRemainder;
    private int periodOfMonthsCount = 0;


    // ** Set GUI Form field values
    public double interestRate; // supplied from gui
    public double savingsAmount; // supplied from gui
    public int period; // supplied from gui
    public String periodYearsMonths; // supplied from gui
    public String interestAdded; // supplied from gui

    public void helloWorld(){
        System.out.println("== Hello my.custom ==");
    }

    // ++ Runs via a loop from this.calculatePeriodInterestRateTotals(loopcount)
    private void calculatePeriodInterestRateTotalsProcessLoop(int loopcount)
    {

        if(this.getPeriodYearsMonthsFormValue().equals("Months"))
        {
            this.incrementPeriodOfMonthsCount();
        }

        String interestAddedVal = this.getInterestAddedFormValue();

        //System.out.println("== interestAddedVal: " + interestAddedVal + "==\n");

        switch (interestAddedVal)
        {
            case "Annual":
            case "Annually":
                this.calcInterestPeriodFromAnnualInterest(loopcount);

                break;
            default:
                this.calcInterestPeriodFromYMonthlyInterest(loopcount);

        }

    }

    private void calcInterestPeriodFromYMonthlyInterest(int loopcount)
    {
        double amount = this.getSavingsAmountFormValue();
        double lastPeriodValue = this.getLastPeriodAmount();
        if( lastPeriodValue != 0.0 )
        {
            // calculate interest rate on the last savings period amount that was saved
            //System.out.println(" -- calculate interest rate on Last period amount: " + this.getLastPeriodAmount());
            amount = lastPeriodValue;

        }

        double interestThisPeriod = 0.0;
        double monthlyInterest;
        double interestMonthly;
        interestMonthly = (this.getInterestRateFormValue() / 12);
        if(this.getPeriodYearsMonthsFormValue().equals("Months") )
        {
            //System.out.println("!! calcInterestPeriodFromYMonthlyInterest: " + amount + " | getPeriodYearsMonthsFormValue().equals(\"Months\")  ");
            //this.calcInterestMonthlyFromMonthlyInterest(loopcount, amount);
            monthlyInterest = (amount * interestMonthly / 100);
            amount = (amount + monthlyInterest);

            interestThisPeriod = (interestThisPeriod + monthlyInterest);
        }
        else
        {
            for(int i = 0; i < 12; i++)
            {
                monthlyInterest = (amount * interestMonthly / 100);
                amount = (amount + monthlyInterest);

                interestThisPeriod = (interestThisPeriod + monthlyInterest);

            }
        }

        // System.out.println(" * This period (MONTHLY):  period amount: " + amount + " x interest rate, " + interestRate + " = " + interestThisPeriod + " *");
        this.setPeriodInterestArray(interestThisPeriod);
        this.setPeriodInterestSumArray(interestThisPeriod);
        this.setPeriodBalancePlusInterestArray((amount));
        this.setLastPeriodAmount((amount));

    }
    // ++MAY NOT BE NECESSARY
    private void calcInterestMonthlyFromMonthlyInterest(int loopcount, double amount)
    {
        //System.out.println("== calcInterestMonthlyFromMonthlyInterest cycle: " + loopcount + " ==");
    }

    // Runs on a loop sent from calculatePeriodInterestRateTotals() -> calculatePeriodInterestRateTotalsProcessLoop()
    private void calcInterestPeriodFromAnnualInterest(int loopcount)
    {

        //System.out.println("== calcInterestPeriodFromAnnualInterest cycle: ==\n");
        double amount = this.getSavingsAmountFormValue();
        double lastPeriodValue = this.getLastPeriodAmount();

        if( lastPeriodValue != 0.0 )
        {
            amount = lastPeriodValue;

        }

        if(this.getPeriodYearsMonthsFormValue().equals("Months") )
        {
            this.calcInterestMonthlyFromAnnualInterest(loopcount, amount);
        }
        else
        {

            // System.out.println(" ** Get this.getPeriodYearsMonthsFormValue() DEBUG " + this.getInterestAddedFormValue() + " **");
            //double yearTotal = 0.0;
            double interestThisPeriod = (amount * this.getInterestRateFormValue() / 100);

            //System.out.println(" * ** Get getPeriodFormValue DEBUG " + this.getPeriodFormValue() + " ** *");
            this.setPeriodInterestArray(interestThisPeriod);
            this.setPeriodInterestSumArray(interestThisPeriod);
            this.setPeriodBalancePlusInterestArray((amount + interestThisPeriod));
            this.setLastPeriodAmount((amount + interestThisPeriod));
            //System.out.println(" * ** Interest this period "+ interestThisPeriod +" | Amount: " + amount + "** *");
        }

    }
    // ++ Use if 'Interest Added' form value is set to monthly
    // Loops according to period value  'foreach' loop sent from this.calculatePeriodInterestRateTotals();
    //private void calcInterestMonthlyFromAnnualInterest(int loopcount, double amount, double lastPeriodValue)
    private void calcInterestMonthlyFromAnnualInterest(int loopcount, double amount)
    {
        int periodValue = this.getPeriodFormValue();
        // *  Use modulus to calculate whether to add interest for the year to the last month of the total selected months or the 12th month
        if(loopcount % 12 == 0){

            // Add interest to the year end (12th month)
            //System.out.println("== calcInterestMonthlyFromAnnualInterest cycle: " + loopcount + " | * Add Interest to final month of year * ==");

            double interestThisPeriod = (amount * this.getInterestRateFormValue() / 100);

            //System.out.println(" * ** Get getPeriodFormValue DEBUG " + this.getPeriodFormValue() + " ** *");
            this.setPeriodInterestArray(interestThisPeriod);
            this.setPeriodInterestSumArray(interestThisPeriod);
            this.setPeriodBalancePlusInterestArray((amount + interestThisPeriod));
            this.setLastPeriodAmount((amount + interestThisPeriod));
            //System.out.println(" * ** Interest this period "+ interestThisPeriod +" | Amount: " + amount + "** *");
        }
        else if(loopcount % periodValue == 0)
        {

            // Add interest to the final month in the cycle as selected in the Gui total months in period.
            // ++ FIX | Around 7 pence more per month than https://www.thecalculatorsite.com/finance/calculators/savings-calculators.php

            double interestRateFormValueMonth = (this.getInterestRateFormValue() /12);
            int monthsofinterest = periodValue % 12; // get number of months remaining in the year
            interestRateFormValueMonth = interestRateFormValueMonth * monthsofinterest;
            double totalInterestForPeriod = (amount * interestRateFormValueMonth / 100);
            this.setPeriodInterestArray(totalInterestForPeriod);
            this.setPeriodInterestSumArray(totalInterestForPeriod);
            this.setPeriodBalancePlusInterestArray((amount + totalInterestForPeriod));
            //this.setLastPeriodAmount((amount + interestThisPeriod));
            //System.out.println("== calcInterestMonthlyFromAnnualInterest cycle: " + loopcount + " | * Final interest total? - periodvalue " + periodValue +" | Months of year to calculate : " + monthsofinterest + "* ==");
        }
        else
        {
            // Don't calculate interest as not the end of year or not the last possible month in the given total months cycle.
            // System.out.println("== calcInterestMonthlyFromAnnualInterest cycle: " + loopcount + " | Add 0.0 to arrays ==");
            this.setPeriodInterestArray(0.00);
            this.setPeriodInterestSumArray(0.00);
            this.setPeriodBalancePlusInterestArray(0.00);
        }

        //System.out.println("== calcInterestMonthlyFromAnnualInterest cycle: " + loopcount + " ==");

    }

    // Add item to the periodInterest List
    private void setPeriodInterestArray(Double periodInterest)
    {
        this.periodInterest.add(periodInterest);
    }
    // Add item to the periodInterest List
    private void setPeriodInterestSumArray(Double periodInterest)
    {
        if(this.periodInterestSum.isEmpty() || periodInterest == 0)
        //if(this.periodInterestSum.length == 0 || periodInterest == 0)
        {
            this.periodInterestSum.add(periodInterest); // Add first entry of interest period sums
            //this.periodInterestSum[this.periodInterestSum.length] = periodInterest; // Add first entry of interest period sums
            //System.out.println("xx Period interest is empty or zero: "+ periodInterest + " xx");
        }
        else
        {
            int getLast = (this.periodInterestSum.size() -1); // get index of last entry
            //int getLast = (this.periodInterestSum.length -1); // get index of last entry
            Double prevInterest = this.periodInterestSum.get(getLast); // get last interest entry
            //Double prevInterest = this.periodInterestSum[getLast]; // get last interest entry
            this.periodInterestSum.add((prevInterest + periodInterest)); // add last interest amount to new interest amount to get total sum
            //this.periodInterestSum[this.periodInterestSum.length] = (prevInterest + periodInterest); // add last interest amount to new interest amount to get total sum
            //System.out.println("xx Period interest had total: "+ prevInterest + " + " + periodInterest + " xx");
        }

    }

    // Reset periodInterest List
    public void resetPeriodInterestArray()
    {
        this.periodInterest = new ArrayList<>();
    }
    public void resetPeriodInterestSumArray()
    {
        this.periodInterestSum = new ArrayList<>();

    }
    // Add item to the periodBalancePlusInterest List
    private void setPeriodBalancePlusInterestArray(Double periodInterest)
    {
        this.periodBalancePlusInterest.add(periodInterest);
    }

    // Add item to the periodBalancePlusInterest List
    public void resetPeriodBalancePlusInterestArray()
    {
        this.periodBalancePlusInterest = new ArrayList<>();
    }
    // Add item to the periodBalancePlusInterest List
    public ArrayList<Double> getPeriodBalancePlusInterestArray()
    {
        return this.periodBalancePlusInterest;
    }

    // ++ NEW FUNCTIONS - 2019-01 | START ++ //

    // set last period amount for calculating future period interest.
    private void setLastPeriodAmount(double amount)
    {
        this.lastPeriodAmount = amount;
    }
    // RESET last period amount for calculating future period interest to 0.0 if form is re-run.
    public void resetLastPeriodAmount()
    {
        if(this.lastPeriodAmount != 0.0)
        {
            this.lastPeriodAmount = 0.0;
        }

    }
    // get last period amount for calculating future period interest.
    private double getLastPeriodAmount()
    {
        return this.lastPeriodAmount;
    }

    // ++ Cycle through each period calling the appropriate function and adding interest to the arrays.
    public void calculatePeriodInterestRateTotals()
    {
        int periodValue = this.getPeriodFormValue();

        if(this.getPeriodYearsMonthsFormValue().equals("Months"))
        {
            this.setYearsInMonthsPeriodAndRemainder();
        }

        int loopcount = 1;
        for(int i = 0; i < periodValue; i++)
        {
            // ++ Start processing of interest based on the period number form value.
            this.calculatePeriodInterestRateTotalsProcessLoop(loopcount);
            loopcount ++;

        }
    }


    private void setYearsInMonthsPeriodAndRemainder()
    {

        //System.out.println(" ++ Months in Period: " + this.getPeriodFormValue() + " ++");
        this.yearsInMonthsPeriod = this.getPeriodFormValue() / 12;
        this.yearsInMonthsRemainder = this.getPeriodFormValue() % 12;
        //System.out.println(" ++ Years from Total Months Period (" + this.getPeriodFormValue() +") supplied = " + yearsInMonthsPeriod + " with remainder: " + yearsInMonthsRemainder + " ++");
    }

    private void incrementPeriodOfMonthsCount()
    {
        this.periodOfMonthsCount ++;
    }

    public void resetPeriodOfMonthsCount()
    {
        this.periodOfMonthsCount = 0;
    }

    private double getSavingsAmountFormValue()
    {
        return this.savingsAmount;
    }
    private double getInterestRateFormValue()
    {
        return this.interestRate;
    }

    public int getPeriodFormValue()
    {
        return this.period;
    }

    public String getPeriodYearsMonthsFormValue()
    {
        return this.periodYearsMonths;
    }

    private String getInterestAddedFormValue()
    {
        return this.interestAdded;
    }

    public ArrayList getPeriodMonthlyInterest()
    {
        return this.periodMonthlyInterest;
    }

    public ArrayList<Double> getPeriodInterest()
    {
        return this.periodInterest;
    }
    //    public double[] getPeriodInterestSum()
//    {
//        return this.periodInterestSum;
//    }
    public ArrayList<Double> getPeriodInterestSum()
    {
        return this.periodInterestSum;
    }
}
