/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author colino1804
 */


public class Script_timer {
 
    private static long start;
    private static long end;
    
    public Script_timer()
    {
        //System.out.println("- Invoking Script_timer -");
        startTime();
    }
    
    private static void startTime()
    {
        if(start == 0)
        {
            start = System.currentTimeMillis();
        }
        
    }

    private static void endTime()
    {
        if(end == 0)
        {
            end = System.currentTimeMillis();
        }
        
    }    
    public static long getStartTime()
    {
        startTime();
        return start;
    }

    public static long getEndTime()
    {
        endTime();
        return end;
    }
    
    public static String getScriptTime()
    {

        //return end - start;
        long milliend = end - start;
        long milli_convert;
        long milli_convert_seconds;
        
        if(milliend > 1000 * 60)
        {
//            milli_convert = TimeUnit.MILLISECONDS.toMinutes(milliend);
//            //milli_convert = TimeUnit.MILLISECONDS.toSeconds(milliend);
//            return milli_convert + " minutes";
            long minutes = (milliend / 1000)  / 60;
            int seconds = (int)((milliend / 1000) % 60);
            return minutes + " minutes and " + seconds + " seconds";
        }
        else if (milliend > 1000)
        {
            milli_convert = TimeUnit.MILLISECONDS.toSeconds(milliend);
            return milli_convert + " seconds";
        }
        
        return milliend + " milliseconds";
    }
    
}
