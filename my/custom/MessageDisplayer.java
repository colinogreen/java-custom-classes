package my.custom;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import java.lang.String;
import java.util.HashMap;

/**
 *
 * @author Colin M.
 */
final public class MessageDisplayer 
{
        
    private String message_string; //* Use with setMessageString and GetMessageString to send messages back to whatever app (console, GUI, Android ...) is using them
    private String error_list_messages;
    final private HashMap<String, String> error_list = new HashMap<>();
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
    
    //** Error methods (with error_list property | START **//
    
    public void setErrorListItem(String control_name, String error_message)
    {
        this.setErrorListItem(control_name ,error_message, false);
    }
    /**
     * Overload version with the ability to reset the error list, if necessary.
     * @param control_name
     * @param error_message
     * @param reset_error_list 
     */
    public void setErrorListItem(String control_name, String error_message, boolean reset_error_list)
    {
        if(reset_error_list)
        {
            this.resetErrorList();
        }
        this.error_list.put(control_name ,error_message);
    }    
    public int getErrorListCount()
    {
        return this.error_list.size();
    }
    
    public HashMap getErrorListItems()
    {
        return this.error_list;
    }

    
//    public void setErrorListMessage(String control_name, String message)
//    {
//        this.setErrorListItem(control_name, message);
//    }
    
    
    /**
     * Get error messages as a string delimited by new line.
     * @param reset_error_list
     * @return delimited string (of error messages)
     */
    public String getErrorListMessages(boolean reset_error_list)
    {
        return this.getErrorListMessages("\n", reset_error_list);
    }
    
    /**
     * Get error messages as a string delimited by new line. DO NOT reset error list before returning.
     * @return delimited string (of error messages)
     */
    public String getErrorListMessages()
    {
        return this.getErrorListMessages("\n", false);
    }    
    /**
     * Get error messages as a string with the supplied delimiter. reset error list with second parameter, if necessary.
     * @param delimiter
     * @param reset_error_list
     * @return delimited string (of error messages)
     */
    public String getErrorListMessages(String delimiter, boolean reset_error_list)
    {
        this.error_list_messages = "";
        this.error_list.forEach((label,message)->
        {
           this.error_list_messages += message + delimiter;
        });
        String return_messages = this.error_list_messages;
        if(reset_error_list)
        {
            this.resetErrorList(); // Potentially causes problems if this is not done when re-running operation.
        }
        
        return return_messages;
    }
    
    protected void resetErrorList()
    {
        this.error_list.clear();
        //System.out.println("++ DEBUG ++:\nerror_list_clear_results:" + this.error_list.toString() + "\n+++");
    }
    
    public Object[] getErrorListKeysArray()
    {
        return this.error_list.keySet().toArray();
    }
    public Object[] getErrorListValuesArray()
    {
        return this.error_list.values().toArray();
    }
    //** Error methods (with error_list property | END **//
}