package opengdc.consistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

public class AnnotationFileConsistence {
static String annotation_file = "/FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation450/humanMethylation450_annotations.bed";	
// and /FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation27/humanMethylation27_annotations.bed
	
	public static void main(String[] args) {
//input_file is an original file in methylation experiment, choose this file base on Illumina platform of the annotation file (27k or 450k)
		annotationfile2inputfile("/FTP/ftp-root/opengdc/original/tcga/tcga-brca/methylation_beta_value/999ade01-d919-4a78-a503-8e31310a1a1a_jhu-usc.edu_BRCA.HumanMethylation450.8.lvl-3.TCGA-AR-A1AN-01A-11D-A12R-05.gdc_hg38.txt");
	// for 27k 995c114b-5212-4a13-8547-f887c7f39e69_jhu-usc.edu_BRCA.HumanMethylation27.3.lvl-3.TCGA-BH-A0DE-01A-11D-A112-05.gdc_hg38.txt
	}


	//CONTROLLO DEI SITI DELL'ANNOTATION CON UN FILE DI INPUT

	private static void annotationfile2inputfile(String input_file) {
		LinkedHashMap<String, String> annotationMap = new LinkedHashMap<String, String>();

		LinkedHashMap<String, String> mappaInputFile = new LinkedHashMap<String, String>();
		if(annotationMap.isEmpty() ){
			try{
				BufferedReader file = new BufferedReader(new FileReader(annotation_file));
				String line;
				while ((line = file.readLine()) != null) {
					String[] linesplit = line.split("\t");
					if(!annotationMap.containsKey(linesplit[4]))
						annotationMap.put(linesplit[4], line);
					else System.err.println("ERROR duplicate site: "+linesplit[4]);
				}
			}catch(Exception e){

			}

			try{
				BufferedReader file = new BufferedReader(new FileReader(input_file));
				String line;
				while ((line = file.readLine()) != null) {
					String[] linesplit = line.split("\t");
					if(!mappaInputFile.containsKey(linesplit[0]) && !line.contains("Composite"))
						mappaInputFile.put(linesplit[0], line);
					else System.err.println("ERROR duplicate site: "+linesplit[0]);
				}
			}catch(Exception e){

			}

			int conta_siti_esclusi = 0;
			for(String site: mappaInputFile.keySet()){
				if(!annotationMap.containsKey(site)){
					if(mappaInputFile.get(site).split("\t")[1].equals("NA") || mappaInputFile.get(site).split("\t")[2].equals("*"))
						//System.err.println("OK te site "+site+" has no chr or beta_value");
						conta_siti_esclusi ++; 
					else{
						System.err.println("ERROR site "+site+" not in annotation");
						System.err.println("\tline: "+ mappaInputFile.get(site));}
				}
			}
			System.out.println("x -- > #site in input "+mappaInputFile.size()+"\ny -- > #site in annotation "+annotationMap.size()+"\nz -- > #site with no 'chr' or 'beta value' "+conta_siti_esclusi);
			int sum = annotationMap.size() +conta_siti_esclusi;
			System.out.println("y + z = "+ sum);
			if(sum == mappaInputFile.size()){
				System.out.println("OK, the annotation file is correct");
			}else
				System.out.println("NO, the annotation file is not correct");


		}
	}
	
}
