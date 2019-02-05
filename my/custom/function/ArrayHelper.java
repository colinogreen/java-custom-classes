/*
 * Created by Colin Morris on 03-Feb-2019.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom.function;

import java.util.Arrays;

public class ArrayHelper {
    
    // overload String array method
    public static String implode(String delimiter, String... array)
    {
        // This is a PHP Type 'implode' function for Java.
        return ArrayHelper.implodeRun(delimiter, array);

    }
    
    // overload int array method
   public static String implode(String delimiter,int... array)
    {
        String[] intArray = new String[array.length];
        intArray =  convertIntArrayToString(array);
        
        return ArrayHelper.implodeRun(delimiter,intArray);

    }
   
   private static String[] convertIntArrayToString(int[] intArray)
   {
       String[] array = new String[intArray.length];
       //String ret = "";
        for(int i=0;i < array.length;i++)
        {
            array[i] += String.valueOf(intArray[i]);
        }  
       return array;
   }
   
   private static String implodeRun(String delimiter,String[] array)
   {
        System.out.println("Recieved String!");
        String ret = "";
        for(int i=0;i < array.length;i++)
        {
            ret += (i == array.length - 1) ? array[i] : array[i] + delimiter;
        }        
        return ret;
   }

}
