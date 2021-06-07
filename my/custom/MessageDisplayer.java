package my.custom;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import java.lang.String;

/**
 *
 * @author Colin M.
 */
public class MessageDisplayer 
{
        
    private String message_string; //* Use with setMessageString and GetMessageString to send messages back to whatever app (console, GUI, Android ...) is using them
// ** Message methods | START (possibly separate into separate re-usable message class) **//
    
    /**
     * Set or concatenate existing message string;
     * Use default concatenated preceding delimiter of "\n" for newline
     * @param message 
     */
    public void setMessageString(String message)
    {
        this.setMessageStringProcess(message, "\n");
    }
    
    /**
     * Set or concatenate existing message string;
     * Note: second delimiter parameter precedes concatenated message.
     * @param message
     * @param delimiter 
     */
    public void setMessageString(String message, String delimiter )
    {
        this.setMessageStringProcess(message, delimiter);
    }
    /**
     * 
     * @param message
     * @param delimiter 
     */
    private void setMessageStringProcess(String message, String delimiter )
    {
        if(this.message_string == null)
        {
            // Add message for the first time
           this.message_string = message;
        }
        else
        {
            // Add to existing message(s) separated by delimiter that can be 'split' into an array by getMessageArray
            this.message_string += delimiter + message;
        }
    }
    /**
     * Reset message_string variable to null
     */
    public void resetMessageString()
    {
        this.message_string = null;
    }
    /**
     * Reset message_string variable and (if necessary) replace with a new message
     * @param message 
     */
    public void resetMessageString(String message)
    {
        this.message_string = message;
    }
    
    // return message(s) that are potentially separated using newline delimiter
    public String getMessageString()
    {
        return this.message_string;
    }
    
    /**
     * return array of messages, if possible.
     * Default: split by newline (\n). See overload method
     * @return String[] array
     */ 
    public String[] getMessageArray()
    {
        return this.message_string.split("\n");
    }
    /**
     * return array of messages, if possible, according to supplied delimiter
     * @param delimiter
     * @return String[] array
     */ 
    public String[] getMessageArray(String delimiter)
    {
        return this.message_string.split(delimiter);
    }
     // ** Message methods | END **// (possibly separate into separate re-usable message class)
}