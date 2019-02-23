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
    private Boolean saveSuccess;
    JFileChooser file;
    
    // ++ Initiate with Constructor (Change operation for other apps if necessary)
    public File_operations(String displayTextFromApp){
         
        file = new JFileChooser();
        setDisplayTextToExport(displayTextFromApp) ;
        //String saveText = "Hello save world!";
        fileExportSave();
              
    }
    
    private void fileExportSave()
    {
        setShowSaveDialogResult(file.showSaveDialog(null));
        //System.out.println("** DEBUG NEW CLASS: showSaveDialog returns value: '" + result + " **'");
        fileExportSaveProcess();       
    }
    
    private void setSaveSuccess(Boolean saveResult)
    {
        this.saveSuccess = saveResult;
    }
    public Boolean getSaveSuccess()
    {
        return this.saveSuccess;
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
    
    // ++ Process File Export attempt
    private int fileExportSaveProcess()
    {

        if(this.getShowSaveDialogResult() == 0)
        {
            // ** Attempt to export data to file
            this.fileExportSaveProcessAttempt();

        }
        else if(this.getShowSaveDialogResult() ==1){
            this.setSaveSuccess(false) ;
            setResponseMessageText("Export Cancelled!");
        }        
        return this.getShowSaveDialogResult();
    }
    
    // ++ Attempt to export data to file if 'this.getShowSaveDialogResult()' = 0
    private void fileExportSaveProcessAttempt()
    {
        if (this.getShowSaveDialogResult() == JFileChooser.APPROVE_OPTION )
        {
            File saveFile = file.getSelectedFile();
            //System.out.println("Attempting to save text, getDisplayText(): '" + getDisplayTextToExport() + "'");
            try
            {
                //System.out.println("INSIDE try for: '" + saveText + "'"); 
               FileWriter fw = new FileWriter(saveFile);
               //fw.write(saveText);
               fw.write(getDisplayTextToExport());
               
               this.setSaveSuccess(true) ;
               this.setResponseMessageText("File was saved with the name, '" + saveFile.getName() + "'\nat the path, '" + saveFile.getPath() + "'");
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
            this.setSaveSuccess(false) ;
            this.setResponseMessageText("Cannot save file as No calculations appear to have been made!");
            //this.displayResultInTextField();
        }
        else
        {
            this.setSaveSuccess(false) ;
            this.setResponseMessageText("An unknown error occured saving file!");
        }
    }

    private void setShowSaveDialogResult(int res)
    {
        result = res;
    }   
    public int getShowSaveDialogResult()
    {
        return result;
    } 
    
}
