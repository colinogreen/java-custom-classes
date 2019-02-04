package my.custom.function;

import java.util.Arrays;
public class TestOutput {

    public static void main (String args[])
    {
        System.out.println("Hello Test Output") ;
        int[] nums = {5,1,2,11,3}; //List or Vector
        Arrays.sort(nums); //Collections.sort() for List,Vector
        String a=Arrays.toString(nums); //toString the List or Vector
        //String ar[]=a.substring(1,a.length()-1).split(", ");
        System.out.println(a);
    }
//        // overload String array method
//    public static String implode(String delimiter, String... array)
//    {
//        // This is a PHP Type 'implode' function for Java.
//        String ret = "";
//        for(int i=0;i < array.length;i++)
//        {
//            ret += (i == array.length - 1) ? array[i] : array[i] + delimiter;
//        }        
//        return ret;
//    }
}
