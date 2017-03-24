/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed.action;

import gdc2bed.GUI;
import gdc2bed.Settings;
import gdc2bed.parser.BioParser;
import gdc2bed.parser.MaskedSomaticMutation;

/**
 *
 * @author fabio
 */
public class ConvertDataAction extends Action {

    @Override
    public void execute(String[] args) {
        // skip the first entry -> convert
        //String action = args[0];
        String program = args[1];
        String disease = args[2];
        String dataType = args[3];
        String format = args[4];
        String input_path = Settings.getInputGDCFolder();
        String output_path = Settings.getOutputConvertedFolder();
        
        System.err.println("Converting GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Format: " + format + "\n" + "Input Folder Path: " + input_path + "\n" + "Output Folder Path: " + output_path + "\n");
        GUI.appendLog("Converting GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Format: " + format + "\n" + "Input Folder Path: " + input_path + "\n" + "Output Folder Path: " + output_path + "\n");
        
        BioParser parser = null;
        switch (dataType.toLowerCase()) {
            case "masked somatic mutation":
                parser = new MaskedSomaticMutation();
                break;
        }
        parser.setFormat(format);
        parser.convert(program, disease, dataType, input_path, output_path);
        
        GUI.appendLog("\n" + "done" + "\n\n" + "#####################" + "\n\n");
    }
    
}
