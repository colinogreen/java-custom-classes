/*
 * Created by Colin Morris on 29-Jan-2019.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom;

/**
 *
 * @author colino
 */
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Savings_form_process {
// Used by both the Desktop app and the Android App!
    
    public String exceptionMessage = null;
    
     // Arrays that will expand and contract via the 'Private void setExceptionMessageArray(String msg)' method
    private String exceptionArray[] = new String[0];
    private String exceptionArrayTemp[] = new String[0];
    
    public String savings_field;    
    public String interestRate_field;
    public Object period_field;
    public Object periodYearsMonths_field;
    public Object interestAdded_field;
    
    private ArrayList<Double> interestTotals;
    private ArrayList<Double> interestSumTotals;
    //private double[] periodInterestSum = {};
    //private ArrayList<Double> periodMonthlyInterest = new ArrayList<>();

    private ArrayList<Double> balancePlusInterestTotals;

    //private Boolean firstRun = true; // For clearing existing data, etc.

    DecimalFormat decimal = new DecimalFormat(".##");
    private String displayText;
    //private String periodType = "Year";

    // ++ GET THE 'Savings_interest_engine' Class that builds the calculations ++
    Savings_interest_engine savings = new Savings_interest_engine();
    
    // Constructor (if necessary)
    public Savings_form_process()
    {
        //System.out.println("*** HELLO FROM EXTERNAL CLASS "+ this.getClass().getName() +" !!!  ****");
    }
    
    // Create ExceptionMessage array that can be added to dynamically (or probably should just use ArrayList object!).
    private void setExceptionMessageArray(String msg)
    {
        int arrayCount = exceptionArray.length;
                
        exceptionArrayTemp = exceptionArray.clone(); // Copy existing exceptionArray to temporary version
        exceptionArray = new String[arrayCount + 1]; // recreate brand new exceptionArray with new size for the new message;       
        
        if(arrayCount > 0)
        {
            // if the copied original array had contents, its worth copying the array copy to the new array with the new length
            System.arraycopy( exceptionArrayTemp, 0, exceptionArray, 0, exceptionArrayTemp.length );           
        }

        // Now, add the latest exception message to the end of the new array
        exceptionArray[arrayCount] = msg;
    }
    
    public String[] getExceptionMessageArray()
    {
        // return whatever array contents were set using setExceptionMessageArray() method to front-end GUI/form
        return this.exceptionArray;
    }
    
    public void resetExceptionMessage()
    {    
        exceptionMessage = (exceptionMessage != null)? null : exceptionMessage;       
    }
    
    private void setExceptionMessage(String exMsg)
    {
        if(exceptionMessage == null)
        {
            exceptionMessage = exMsg;
        }
        else
        {
            // add to existing message
            exceptionMessage += "\n" + exMsg;
        }
        
    }
    
    public String getExceptionMessage()
    {
        // If this is not 'null' anymore, an exception was set.
        return exceptionMessage;
    }
    
    private void setInterestTotals(ArrayList<Double> arrayList)
    {
        interestTotals = arrayList;
    }

    public ArrayList<Double> getInterestTotals()
    {
        return interestTotals;
    }

    private void setInterestSumTotals(ArrayList<Double> arrayList)
    {
        interestSumTotals = arrayList;
    }
    
    public ArrayList<Double> getInterestSumTotals()
    {
        return interestSumTotals;
    }
    
    private void setBalancePlusInterestTotals(ArrayList<Double> arrayList)
    {
        balancePlusInterestTotals = arrayList;
    }
    
    public ArrayList<Double> getBalancePlusInterestTotals()
    {
        return balancePlusInterestTotals;
    }

    // ** SET Form field values from GUI FORM **
    private void setFormFieldCurrentValues(
            String savings_field,
            String interestRate_field,
            Object period_field,
            Object periodYearsMonths_field,
            Object interestAdded_field)
    {
        savings.savingsAmount = this.convertTypeToDouble(savings_field);
        savings.interestRate = this.convertTypeToDouble(interestRate_field);
        savings.period = this.convertTypeToInt(period_field);
        savings.periodYearsMonths = this.convertTypeToString(periodYearsMonths_field);
        savings.interestAdded = this.convertTypeToString(interestAdded_field);

    }
    
    // Set public interest and Savings Sum totals returned from Savings_interest_engine_LOCAL for retrieval by Main Class
    //private void setPublicInterestTotalArraysLists(ArrayList intTotals,ArrayList intSumTotals, ArrayList balPlusIntTotals)
    private void setPublicInterestTotalArraysLists()
    {
        setInterestTotals(savings.getPeriodInterest());  
        setInterestSumTotals(savings.getPeriodInterestSum());  
        setBalancePlusInterestTotals(savings.getPeriodBalancePlusInterestArray());
    }

    private void buildResultText(){
        resetDisplayText(); // reset text for display on form to null, if
        String PeriodType = savings.getPeriodYearsMonthsFormValue(); // Get selected period type (Years or Months)
        PeriodType = PeriodType.substring(0,(PeriodType.length() -1)); // remove plural (s) from whichever periodType is returned.

        String displayTotals = PeriodType + "\tInterest\tTotal Interest\tPeriod Total\n"; // Build column titles

        for(int i = 0; i < savings.getPeriodInterest().size(); i++)
        {

            //System.out.println("Debug returned interest: " + decimal.format(savings.getPeriodInterest().get(i)) + " | Total: " + savings.getPeriodBalancePlusInterestArray().get(i));
            displayTotals += (i + 1) + "\t"+ decimal.format(savings.getPeriodInterest().get(i))
                    + "\t"+ decimal.format(savings.getPeriodInterestSum().get(i)) + "\t" + decimal.format(savings.getPeriodBalancePlusInterestArray().get(i));
            displayTotals += "\n"; // new line before next set of results.
        }
        this.setDisplayText( displayTotals) ;

    }
    

    // reset display text to null
    private void resetDisplayText()
    {
        this.displayText = null;
    }
    // Set the display text for returning to the GUI
    private void setDisplayText(String text){
        if(this.displayText != null)
        {
            this.displayText = this.displayText + "\n" + text;
        }
        else
        {
            this.displayText = text;
        }

    }
    // Get the display text for returning to the GUI
    public String getDisplayText()
    {
        return this.displayText;
    }

    // ++ NEW FUNCTIONS - 2019-01 | END  ++ //

    // ** Type converters | START **

    // Method overload, if necessary..
    private double convertTypeToDouble(String str)
    {
        try
        {
            return Double.valueOf(str);

        }
        catch(Exception e)
        {
            System.out.println("Exception occurred:\n" + e.getMessage());

            this.setExceptionMessage("Not a number: " + e.getMessage()); // ++ REPLACE WITH BELOW ++
            this.setExceptionMessageArray("Not a number: " + e.getMessage());
            //this.showMessagePopup(eString, "Exception occurred:");

        }

        return 0.0;

    }

    // Method overload, if necessary..
    private int convertTypeToInt(String str)
    {
        return Integer.parseInt(str);
    }


    // Method overload, if necessary..
    private int convertTypeToInt(Object obj)
    {
        String conv = String.valueOf(obj);
        //return (Integer)obj;
        return Integer.parseInt(conv);
    }

    // Method overload, if necessary..
    private String convertTypeToString(int str)
    {
        return String.valueOf(str);
    }

    // Method overload, if necessary..
    private String convertTypeToString(Object obj)
    {
        return String.valueOf(obj);
    }

    // ** Type converters | END **

    // ** Calculations start once the 'Calculate Interest' Button is pressed and the code in this method runs. **
    public void CalculateInterestInitiate() {
        // TODO add your handling code here:
        

        savings.resetPeriodInterestArray();// Reset arraylist if not first run...

        savings.resetPeriodBalancePlusInterestArray();// Reset arraylist if not first run...
        savings.resetPeriodOfMonthsCount(); // Reset just in case we have started again and the count is not zero for monthly period calc

        // Use one setter to set the values entered into the form for retrieval via get...() methods.
        this.setFormFieldCurrentValues(
                savings_field,
                interestRate_field,
                period_field,
                periodYearsMonths_field,
                interestAdded_field
        );
        savings.resetPeriodInterestSumArray(); // Reset resetPeriodInterestSumArray if not first run ONLY AFTER SETTING THE PERIOD VALUE FIELD...
        savings.resetLastPeriodAmount(); // ++ Reset Last period amount to zero if necessary (If 'Calculate Interest' button is pressed more than once)


        savings.calculatePeriodInterestRateTotals();

        this.setPublicInterestTotalArraysLists(); // Set the public Savings total and interest arrays... 
        //... following the calculations in the Savings_interest_engine_LOCAL (savings) class.
        this.buildResultText();

    }


}
