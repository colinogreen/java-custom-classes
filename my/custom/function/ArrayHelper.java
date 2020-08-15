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
   
    public static void sort(String []s, int n) 
    { 
        for (int i=1 ;i<n; i++) 
        { 
            String temp = s[i]; 

            // Insert s[j] at its correct position 
            int j = i - 1; 
            while (j >= 0 && temp.length() < s[j].length()) 
            { 
                s[j+1] = s[j]; 
                j--; 
            } 
            s[j+1] = temp; 
        } 
    } 

    // Function to print the sorted array of string 
    public static void printArraystring(String str[], int n) 
    { 
        for (int i=0; i<n; i++) 
        {
            System.out.print(str[i]+" "); 
        }
         System.out.println();
    } 

//    // Driver function 
//    public static void main(String args[]) 
//    { 
//        String []arr = {"GeeksforGeeks", "I", "from", "am"}; 
//        int n = arr.length; 
//
//        // Function to perform sorting 
//        sort(arr,n); 
//        // Calling the function to print result 
//        printArraystring(arr, n); 
//
//    } 

}
