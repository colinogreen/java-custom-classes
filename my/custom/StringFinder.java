/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import string_search.String_search;
import my.custom.function.ArrayHelper;

/**
 *
 * @author colino1804
 */
public class StringFinder 
{
    Pattern regexp;
    Matcher matcher;

    private String pattern; 
    public String[] pattern_array;
    //ArrayList<String> list = new ArrayList<>();

    public int pattern_match_count = 0;
    public int position_pattern_match_count = 0;
    private String[] items;
    private final Boolean[] process_search_string_length = new Boolean[8]; // if array position is true
    private final int[] process_search_string_length_count = new int[8]; // if array position is true
    private int string_length; // defaults to zero   

    private final int loop_report_divisor = 250000000;

    private Positions pos1;
    private Positions pos2;
    //private String pos2;
    private Positions pos3;
    private Positions pos4;
    private Positions pos5;
    private Positions pos6;
    //private String pos6_str;
    //private int pos6_array_item;

    private Positions pos7;
    private Positions pos8;   
    
    /**
     * This internal object keeps track of each string position
     */
    private class Positions
    {
        
        int pos_array_item = 0;
        String pos = "";
        
    }

    /**
     * Constructor
     * @param args Command line arguments from calling class / main
     */
    public StringFinder(String[] args)
    {

        paramSort(args);

        setSearchList();
    }
    
    public void setStringLength(int length)
    {
        string_length = length;
    }
    
    
    private void createSearchListRegExp()
    {
        this.stringSearchList();
        this.createPatternStringFromArray(pattern_array, false);
        //this.setDefaultProcessSearchStringVals(true);
    }
    
    private void stringSearchList()
    {
        pattern_array = new String[]
        { 
            "the", 
            "laaaaa", 
//            "mzzzzz", 
            "crack", 
            "foxy",
            "jimps",
            "sssttt",
//            "zzzzzz",
            "caazzzz",
            "adamant",
            "ayrshir",
            "over",
            "that",
            "lazy",
            "doggo",
            "bolton",
        };
    }

    /**
     * Create Pattern from the pattern_array array either from supplied args or from method: stringSearchList();
     * Finally, create Pattern.compile regexp object.
     * @param array
     * @param alter_search_length
     * @return 
     */
    private Boolean createPatternStringFromArray(String[] array, Boolean alter_search_length)
    {
        if(array.length == 0)
        {
            return false;
        }
        pattern_array = new String[array.length];
        for (int x = 0; x < array.length; x++)
        {
            incrementSearchStringLengthCountByItem(array[x].length() -1);
            pattern_array[x] = "^" +array[x]  + "$";
            if(array[x].length() > string_length)
            {
                //System.out.println("+* paramSort method : '" + args[x] + "' args[x].length(): " + args[x].length()+ " *+");
                string_length = array[x].length();

            }
            if(alter_search_length == true)
            {
                process_search_string_length[array[x].length() -1] = true;
            }
            

        } 
        this.createPatternStringFromArrayComplete(); // Create the final regexp pattern and initialise regular expression objects
        return true;
    }
    
    /**
     * Create the final regexp pattern and initialise regular expression objects
     */
    private void createPatternStringFromArrayComplete()
    {
        pattern = ArrayHelper.implode("|",pattern_array);
        regexp = Pattern.compile(pattern); // * Definitely quicker to run once here rather than up to multi-billions of times as originally in search loop.
        matcher = regexp.matcher(""); // * May be quicker to run once here rather than potentially multi-billions of times as originally in search loop.        
    }
    
    private void paramSort(String[] args)
    {

        if(args.length > 0)
        {
            setDefaultProcessSearchStringVals(false);
            System.out.println("++ args.length :" + args.length  + " ++");
            //search_string_length = new Boolean[args.length];
            ArrayHelper.sort(args, args.length);
            
            createPatternStringFromArray(args, true);
            
            System.out.println("++ Pattern will be:" + pattern + " ++");
            System.out.println("== Command line args pattern_array ==");
            ArrayHelper.printArraystring(args, args.length);
        }
        else
        {
            createSearchListRegExp();
            setDefaultProcessSearchStringVals(true);

        }
        debugSearchStringData(args);

    }
    
    private void setDefaultProcessSearchStringVals(Boolean true_false)
    {
        for(int i= 0; i < 8; i++)
        {
            process_search_string_length[i] = true_false;
        }        
    }
    
    private void incrementSearchStringLengthCountByItem(int val)
    {
        //System.out.println("Method: incrementSearchStringLengthCountByItem: " + val);
        System.out.println("process_search_string_length_count string length:" + (val +1));
        
        //System.exit(0);
        process_search_string_length_count[val] ++;
        System.out.println(process_search_string_length_count[val]);
      
    }
    // * User with position_pattern_match_count 
    private int getSearchStringLengthCountByItem(int val)
    {
        return process_search_string_length_count[val];
    }
    
    private int[] getSearchStringLengthCounts()
    {
        return process_search_string_length_count;
    }
    
    public void debugSearchStringData(String[] args)
    {
        System.out.println("\n== debugSearchStringData | START ==\n");     
        System.out.println("-- Command line args - sorted --"); 
        for (int a = 0; a < args.length; a++)
        {
            System.out.println(a + ": " +args[a]);
        }
            
        //System.out.println(args);     
        System.out.println("-- process_search_string_length boolean selection --");  
        
        for (int s = 0; s < process_search_string_length.length; s++)
        {
            System.out.println((s + 1) + ": " + process_search_string_length[s]);
        }
        System.out.println("== debugSearchStringData | END ==\n"); 
        //System.out.println("-- Command line args - sorted --");     
    }

    
  
    /**
     * Set all the items in the array that are to be searched.
     */
    private void setSearchList()
    {
//        items = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", // Normal alphabetical order.
//            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        items = new String[]{"j", "a", "c", "d", "e", "f", "g", "h", "i", "b", "k", "l", "m", // Changed order test: j < a < b
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    }
    /**
     * Get the items in the search list, if necessary.
     * @return items
     */
    private String[] getSearchList()
    {
        return this.items;
    }
    /**
     * Get a particular search list item.
     * @param item
     * @return individual item
     */        
    private String getSearchListItem(int item)
    {
        return this.items[item];
    }    
    public double getSearchListCount()
    {
        return items.length;
    }
    
    public double getTotalSearchPositions()
    {
        if(string_length == 0)
        {
            return getSearchListCount();
        }
        return string_length;
    }
    
    // Return total of array string elements to be evaluated.
    private double getPositionLoopCount(int position)
    {
        return Math.pow(getSearchListCount(),position);
    }

    private double calcSearchListCountToPower(int position)
    {
        return getPositionLoopCount(position);
    }

    
    public void processStart()
    {
        processStringByStringLength();
    }
    
    // ** Starts process of finding strings according to incremental string lengths
    private void processStringByStringLength()
    {
        //System.out.println("+* processStringByStringLength method string_length: " + string_length + " *+");
        //position_pattern_match_count getSearchStringLengthCountByItem
        
        for(int i=1; i<= string_length; i++)
        {        
            position_pattern_match_count = 0;
            //System.out.println("* processStringByStringLength will try to match getSearchStringLengthCountByItem( " + i + ") x " + getSearchStringLengthCountByItem(i -1) +" ++");
            //pos1_array_item = 0;
            if(process_search_string_length[i -1] == false)
            {
                // If string length search should be ignored, move on to the next string length
                continue;
            }

            //printMessage("\n-- processStringByStringLength: call printStringBlock: Process string length: " + i + " --");
            printStringBlock(i);        
        }
    }
    
    private void printStringBlock(int position_length)
    {
       double section_loops_total = getPositionLoopCount(position_length);  // * int:position_block_length?
       printMessage("-- printStringBlock: Process string block x section_loops_total (" + section_loops_total + ") | string_length:  " + position_length + " | match strings total :" + getSearchStringLengthCountByItem(position_length -1) + " --\n");
        //for(int i = 0; i< getSearchListCount(); i++)
        //pos1 = "";
        pos1 = new Positions();
        //System.out.println("== Debug : build pos1 Class ==");
        pos2 = new Positions();
        pos3 = new Positions();
        pos4 = new Positions();
        pos5 = new Positions();
        pos6 = new Positions();
        //pos6_str = "";
        //pos6_array_item = 0;
        pos7 = new Positions();
        pos8 = new Positions();
        //position_pattern_match_count = 0;
        //** Intiate printing the string to compare based on the number positions (length of string)
             
        for(double loop_number = 0; loop_number < section_loops_total; loop_number++)
        {
           //** If all similar length strings have been matched for the numbers of strings in this section search, stop the loop on move to the next string length section.
            if(getSearchStringLengthCountByItem(position_length -1) == position_pattern_match_count)
            {
                System.out.println("\nXX ALL MATCHED for strings of " + position_length + " characters in length at loop number, " + loop_number + ": Break to next section XX\n");
                break;
            } 
            printStringBlockStringByLength(position_length, loop_number + 1);

        }
    }
    
    private void printStringBlockStringByLength(int position_block_length, double loop_number)   // * int:position_block_length?
    {
        //** Start printing the string to compare based on the number positions (length of string)

        //string_eval = "";
        for(int i= 1; i<= position_block_length; i++)
        {
            incrementSearchListItem(i,loop_number);
            
        }
        printStringBlockDisplayString(loop_number);
    }
    
    // ** Before displaying full string, increment those parts of the string that need incrementing.
    private void incrementSearchListItem(int string_item, double loop_number)
    {

        switch (string_item)
        {
            case 1:
                getNextDisplayStringItemGen(pos1, loop_number, true, string_item - 1);
                break;
            case 2:
                getNextDisplayStringItemGen(pos2, loop_number, false, string_item - 1);
                break;
            case 3:
                getNextDisplayStringItemGen(pos3, loop_number, false, string_item - 1);
                break;
            case 4:
                getNextDisplayStringItemGen(pos4, loop_number, false, string_item - 1);
                break;
            case 5:
                getNextDisplayStringItemGen(pos5, loop_number, false, string_item - 1);
                break;
            case 6:
                getNextDisplayStringItemGen(pos6, loop_number, false, string_item - 1);
                //getNextDisplayStringItem6(loop_number);
                break;
            case 7:
                getNextDisplayStringItemGen(pos7, loop_number, false, string_item - 1);
                break;
            default:
                getNextDisplayStringItemGen(pos8, loop_number, false, string_item - 1);
                
        }

    }

    /**
     * Main method for incrementing or re-setting the character position array or keeping the character the same depending on the position
     * @param position
     * @param loop_number
     * @param ignore_modulus
     * @param string_item
     * @return 
     */
    private boolean getNextDisplayStringItemGen(Positions position, double loop_number, boolean ignore_modulus, int string_item)
    {

        double modulus_divide = calcSearchListCountToPower(string_item);
        
        //** Repeat same character and return until mod divide length is matched, moving on to the normal increment
        if(!ignore_modulus && loop_number % modulus_divide != 0)
        {
            position.pos = getSearchListItem(position.pos_array_item);
            return false;            
        }
        //** Now we have reached the normal increment / reset process **//
        //** Increment if not at the end of the character array
        if(position.pos_array_item < getSearchListCount() - 1)
        {
            position.pos = getSearchListItem(position.pos_array_item); 
            position.pos_array_item++;
            return true;
        }
        
        //** reset to zero (first char) if end of array of characters is reach
        position.pos = getSearchListItem(position.pos_array_item); 
        position.pos_array_item = 0; // reset to zero
        return false;
        
    }
         
    //** Display the full string
    private void printStringBlockDisplayString(double loop_number)
    {

         // Now populate the matcher object that was created in method:createPatternStringFromArrayComplete with full string ...
        // ... built by the string search loops.
         matcher.reset(this.getStringBlockDisplayString());

        if(matcher.find())
        {
            // Dislay the string that was found by the search.
            printMessage("* found '" + this.getStringBlockDisplayString() + "' at loop # " + loop_number);
            position_pattern_match_count++;// getSearchStringLengthCountByItem
            //System.out.println("* printStringBlockDisplayString Found position_pattern_match_count: " + position_pattern_match_count + "++");
            pattern_match_count++;
            //System.out.println("Note: continuing with pattern_match_count:" + pattern_match_count + " and pattern_array.length: " + pattern_array.length + "!");
            
        }

        if(loop_number % loop_report_divisor == 0)
        {
            System.out.println("Searching: Reached loop # " + loop_number + " in this cycle. (+" + loop_report_divisor + ")");
            
        }
        
        if(pattern_match_count == pattern_array.length)
        {
            this.endSearchAndShowSummary(loop_number);
        }

        //printMessage("printStringBlockDisplayString: " + string_eval);
    }
    //** Display/Compare the full string
    private String getStringBlockDisplayString()
    {
        return pos8.pos + pos7.pos + pos6.pos + pos5.pos + pos4.pos + pos3.pos + pos2.pos + pos1.pos;
    }
    
    private void endSearchAndShowSummary(double loop_number)
    {
            System.out.println("==========================================================");
            System.out.println("HALT! All strings matched. Ending at loop: " + loop_number);
            System.out.println("===========================================================");
            Script_timer.getEndTime();
            System.out.println("-- Script completed: " + Script_timer.getScriptTime() + " --");
            System.exit(0);
    }
    // Has 'overloaded' equivalent
    private void printMessage(String msg)
    {
        System.out.println(msg);
    }
    
    // Has 'overloaded' equivalent
    private void printMessage(String msg, boolean carriage_return)
    {
        if(carriage_return)
        {
            System.out.println(msg);
        }
        else
        {
            System.out.print(msg);
        }
        
    }
    
}
