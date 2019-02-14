/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.custom;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;

/**
 *
 * @author Colin M.
 */
public class File_operations {
    
    int result;
    private String displayTextToExport;
    private String responseMessageText;
    JFileChooser file;
    public File_operations(String displayTextFromApp){
         
        file = new JFileChooser();
        setDisplayTextToExport(displayTextFromApp) ;
        //String saveText = "Hello save world!";
        fileExportSave();
              
    }
    
    private void fileExportSave()
    {
            //JFileChooser file = new JFileChooser();
            //String saveText = "Hello save world!";
            setShowSaveDialogResult(file.showSaveDialog(null));
            System.out.println("** DEBUG NEW CLASS: showSaveDialog returns value: '" + result + " **'");
            fileExportSaveProcess();       
    }
    
    private void setDisplayTextToExport(String text)
    {
        this.displayTextToExport = text;
    }
    private String getDisplayTextToExport()
    {
        return this.displayTextToExport;
    }

    private void setResponseMessageText(String text)
    {
        this.responseMessageText = text;
    }
    public String getResponseMessageText()
    {
        return this.responseMessageText;
    }     
    private int fileExportSaveProcess()
    {

        if(result == 0)
        {
            //show_information("Returned: " + res, "JFileChooser action");
            //try{
                //if (res == JFileChooser.APPROVE_OPTION &&( !getDisplayText().equals("") || getDisplayText() != null))
                if (result == JFileChooser.APPROVE_OPTION )
                {
                  File getFile = file.getSelectedFile();
                  System.out.println("Attempting to save text, getDisplayText(): '" + getDisplayTextToExport() + "'");
                  try
                  {
                      //System.out.println("INSIDE try for: '" + saveText + "'"); 
                      FileWriter fw = new FileWriter(getFile);
                       //fw.write(saveText);
                       fw.write(getDisplayTextToExport());
                       fw.close();
                  }
                  //catch(IOException e)
                  catch(Throwable any)
                  {
                      //this.setDisplayText(e.getMessage());
                      this.setResponseMessageText("Error:\n" + any);
                      //this.displayResultInTextField();
                  }


                }
                else if(getDisplayTextToExport() == null || getDisplayTextToExport().equals("") )
                {
                    this.setResponseMessageText("Cannot save file as No calculations appear to have been made!");
                    //this.displayResultInTextField();
                }          
            //}
            //catch(Throwable any)
            //{
                //show_exception("Error:\n" + any, "Exception encountered");
            //}
        }
        else if(result ==1){
            setResponseMessageText("Export Cancelled!");
        }        
        return result;
    }
    
//    public void showSaveDialog()
//    {
//        res = file.showSaveDialog(null); 
//    }
    
    private void setShowSaveDialogResult(int res)
    {
        result = res;
    }   
    public int getShowSaveDialogResult()
    {
        return result;
    }
    
    
    
    
    
}
