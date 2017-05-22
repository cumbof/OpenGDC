/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.action;

import opengdc.GUI;
import opengdc.Settings;
import opengdc.parser.BioParser;
import opengdc.parser.CopyNumberSegmentParser;
import opengdc.parser.GeneExpressionQuantificationParser;
import opengdc.parser.IsoformExpressionQuantificationParser;
import opengdc.parser.MaskedSomaticMutationParser;
import opengdc.parser.MetadataParser;
import opengdc.parser.MethylationBetaValueParser;
import opengdc.parser.MiRNAExpressionQuantificationParser;

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
        
        BioParser parser;
        switch (dataType.toLowerCase()) {
            case "clinical and biospecimen supplements":
                parser = new MetadataParser();
                break;
            /*case "clinical supplement":
                parser = new MetadataParser();
                break;
            case "biospecimen supplement":
                parser = new MetadataParser();
                break;*/
            case "masked somatic mutation":
                parser = new MaskedSomaticMutationParser();
                break;
            case "mirna expression quantification":
                parser = new MiRNAExpressionQuantificationParser();
                break;
            case "gene expression quantification":
                parser = new GeneExpressionQuantificationParser();
                break;
            case "copy number segment":
                parser = new CopyNumberSegmentParser();
                break;
            case "masked copy number segment":
                parser = new CopyNumberSegmentParser();
                break;
            case "methylation beta value":
                parser = new MethylationBetaValueParser();
                break;
            case "isoform expression quantification":
                parser = new IsoformExpressionQuantificationParser();
                break;
            default:
                parser = null;
                break;
        }
        parser.setFormat(format.toLowerCase());
        int exit_code = parser.convert(program, disease, dataType, input_path, output_path);
        
        System.err.println("\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
        GUI.appendLog("\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
    }
    
}
