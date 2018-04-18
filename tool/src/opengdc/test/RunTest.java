/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.test;

import java.io.File;

import javax.swing.JTextPane;
import opengdc.GUI;
import opengdc.action.Action;
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
public class RunTest extends Action {

	private static  JTextPane logPane = null;

	public static void main(String[] args) {
		// skip the first entry -> convert
		//String action = args[0];
		//        String program = args[1];
		//        String disease = args[2];
		//        String dataType = args[3];
		//        String format = args[4];
		//        String input_path = Settings.getInputGDCFolder();
		//        String output_path = Settings.getOutputConvertedFolder();

		/*gene expression*/ //String[] tumors = {"SARC","STAD"};
		/*clinical and biospecimen*/ //String[] tumors ={"UVM","UCS","UCEC","MESO","LUAD","LIHC","GBM","CESC","BLCA"};
		/*methylation*/ //String[] tumors ={"HNSC","PRAD","STAD"}; //{"KIRC","SKCM"};
		/*mirna*/ //String[] tumors = {"SARC","THYM","STAD","LUSC"};
		/*missing*/ String [] tumors = {"OV"};//{"UVM","UCS","UCEC","TGCT","PCPG","MESO","LUAD","LIHC","KIRP","GBM","CESC","BLCA"};
		/*isoform*/  //String[] tumors = {"STAD","LAML","HNSC","KIRC", "LUSC"};
		String[] experiments = {"Clinical and Biospecimen Supplements"};//{"Gene Expression Quantification", "miRNA Expression Quantification", "Isoform Expression Quantification"};//, "Copy Number Segment", "Masked Copy Number Segment", "Masked Somatic Mutation"};
		for(String tumor: tumors){
			String program = "TCGA";//args[1];
			String disease = "TCGA-"+tumor;//args[2];
			System.out.println("start "+disease);
			for(String dataType: experiments){
				System.out.println("\t "+dataType);
				String format = "META";//args[4];
//				String input_path = "/FTP/ftp-root/opengdc/original/tcga/tcga-"+tumor.toLowerCase()+"/"+dataType.replaceAll(" ", "_").toLowerCase()+"/" ;//Settings.getOutputConvertedFolder();*/
//				String output_path = "/FTP/ftp-root/opengdc/bed/tcga/tcga-"+tumor.toLowerCase()+"/"+dataType.replaceAll(" ", "_").toLowerCase()+"/" ;//Settings.getOutputConvertedFolder();*/
								String input_path = "/FTP/originalMetaOV/";
								String output_path = "/FTP/metaOV/";
				File f = new File(output_path.substring(0,output_path.length()-1));
				if (!f.exists()){
					f.mkdirs();
				}
				// create log window
				//logPane = GUI.createLogWindow();

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
					parser.setFormat(format.toLowerCase());
					parser.setLogger(logPane);
					exit_code = parser.convert(program, disease, dataType, input_path, output_path);
				}

				System.err.println("\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
				GUI.appendLog(logPane, "\n" + "done with exit code " + exit_code + "\n\n" + "#####################" + "\n\n");
			}
		}
	}
	@Override
	public void execute(String[] args) {
		// TODO Auto-generated method stub

	}
}
