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
        System.out.println("Recieved String!");
        String ret = "";
        for(int i=0;i < array.length;i++)
        {
            ret += (i == array.length - 1) ? array[i] : array[i] + delimiter;
        }        
        return ret;
    }
    
    // overload int array method
   public static String implode(String delimiter,int... array)
    {
        // This is a PHP Type 'implode' function for Java.
        System.out.println("Recieved int!");
       // String a[] =Arrays.toString(array); //toString the List or Vector
       // return implodeRun(delimiter, true, a);
        String ret = "";
        for(int i=0;i < array.length;i++)
        {
            ret += (i == array.length - 1) ? array[i] : array[i] + delimiter;
        }        
        return ret;
    }

}
