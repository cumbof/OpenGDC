package opengdc.action;

import javax.swing.JTextPane;
import opengdc.GUI;
import opengdc.Settings;
import opengdc.parser.BioParser;
import opengdc.parser.CopyNumberSegmentParser;
import opengdc.parser.GeneExpressionQuantificationParser;
import opengdc.parser.IsoformExpressionQuantificationParser;
import opengdc.parser.MaskedSomaticMutationParser;
import opengdc.parser.MetadataParserTSV;
import opengdc.parser.MetadataParserXLSX;
import opengdc.parser.MetadataParserXML;
import opengdc.parser.MethylationBetaValueParser;
import opengdc.parser.MiRNAExpressionQuantificationParser;

/**
 *
 * @author fabio
 */
public class ConvertDataAction extends Action {

    private JTextPane logPane = null;
    
    @Override
    public void execute(String[] args) {
        // skip the first entry -> convert
        //String action = args[0];
        String program = args[1];
        String disease = args[2];
        String dataType = args[3];
        String format = args[4];
        boolean enableRecoveryProcedure = true;
        boolean enableUpdateTable = false;
        String updatetable_path = null;
        try {
            if (args[5].trim().toLowerCase().equals("false"))
                enableRecoveryProcedure = false;
            if (args[6].trim().toLowerCase().equals("true"))
                enableUpdateTable = true;
            updatetable_path = args[7];
        }
        catch (Exception e) {}
        String input_path = Settings.getInputGDCFolder();
        String output_path = Settings.getOutputConvertedFolder();
        
        // create log window
        logPane = GUI.createLogWindow();
        System.out.println("CONVERT\tDisease: "+disease+"\tDataType: "+dataType);
        System.err.println("Converting GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Format: " + format + "\n" + "Input Folder Path: " + input_path + "\n" + "Output Folder Path: " + output_path + "\n");
        GUI.appendLog(logPane, "Converting GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Format: " + format + "\n" + "Input Folder Path: " + input_path + "\n" + "Output Folder Path: " + output_path + "\n");
        
        // TODO activate progress bar
        
        BioParser parser;
        switch (dataType.toLowerCase()) {
            case "clinical and biospecimen supplements":
                switch (program.toLowerCase().trim()) {
                    case "tcga":
                        parser = new MetadataParserXML();
                        break;
                    case "target":
                        parser = new MetadataParserXLSX();
                        break;
                    case "fm":
                        parser = new MetadataParserTSV();
                        break;
                    default:
                        parser = null;
                }
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
        
        int exit_code = -1;
        
        if (parser != null) {
            parser.enableRecovery(enableRecoveryProcedure);
            parser.enableUpdateTable(enableUpdateTable);
            parser.setUpdateTablePath(updatetable_path);
            parser.setFormat(format.toLowerCase());
            parser.setLogger(logPane);
            exit_code = parser.convert(program, disease, dataType, input_path, output_path);
        }
        
        System.err.println("\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
        GUI.appendLog(logPane, "\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
    }
    
}
