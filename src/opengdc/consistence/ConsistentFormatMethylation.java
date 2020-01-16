package opengdc.consistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;

public class ConsistentFormatMethylation {
	static String folders_path = "/FTP/ftp-root/opengdc/bed/tcga/";
	static String experiment_folder = "methylation_beta_value";
	static String annotation_file_450k_path = "/FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation450/annotation_450k.bed";
	static String annotation_file_27k_path = "/FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation27/annotation_27k.bed";
	static LinkedHashMap<String, String> annotationMap450k = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> annotationMap27k = new LinkedHashMap<String, String>();
	static int count27 = 0;
	static int count450 = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File[] listOfTumors = new File(folders_path).listFiles();
		for (int i = 0; i < listOfTumors.length; i++) {
			if (listOfTumors[i].isDirectory()) {
				controllDataSchema(listOfTumors[i]+"/"+experiment_folder+"/");
			}
		}
	}

	public static void controllDataSchema(String tumor_experiment){
		try {
			// input the file content to the StringBuffer "input"
			if(tumor_experiment.contains(experiment_folder)){
				File experimentfolder = new File(tumor_experiment);
				if(experimentfolder.exists()){
					System.out.println(experimentfolder.getAbsolutePath());
					for(File fileExperiment : experimentfolder.listFiles()){
						if(fileExperiment.getName().endsWith("bed")){
							//System.out.println(fileTumor.getAbsolutePath());
							BufferedReader file = new BufferedReader(new FileReader(fileExperiment));
							String line;
							//Files.write((new File("/Users/eleonora/Desktop/045d2f0e-5a4c-4246-95da-d6e4429d9c49-mbv.tmp")).toPath(), "".getBytes("UTF-8"), StandardOpenOption.CREATE);
							boolean filecorrupted = false;
							int count = 0;
							while ((line = file.readLine()) != null) {
								count = count +1;
								line = line+"\tendofline";
								String[] fields = line.split("\t");
								if(fields.length-1 == 18){
									if(fields[6].equals("") && !fields[11].equals("")){
										System.err.println("ERROR in line: "+ line + ". all_gene_symbols field exist and gene_symbol not exist. File: "+ fileExperiment.getAbsolutePath());
									}
									if(fields[3].equals("*") && !fields[6].equals("")){
										System.err.println("ERROR in line: "+ line + ". strand field is * but gene_symbol exist. File: "+ fileExperiment.getAbsolutePath());
									}
									filecorrupted = controllConsistenceWithAnnotation(fields, fileExperiment, filecorrupted);
								}else{
									System.err.println("No 18 fields in this line: "+ line + " file: "+ fileExperiment.getAbsolutePath());
								}
							}
							if(filecorrupted)
								System.err.println("Sites not consistent with annotation in file: "+ fileExperiment.getAbsolutePath());

							if(fileExperiment.length() > 50000000){
								if(count450<count){
									System.err.println("too many #line in file: "+fileExperiment.getAbsolutePath()+" ");
								}
							}else{
								if(count27<count){
									System.err.println("too many #line in file: "+fileExperiment.getAbsolutePath()+" ");
								}
							}
							file.close();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	private static boolean controllConsistenceWithAnnotation(String[] fields, File file , boolean filecorrupted) {
		if(file.length() > 50000000){
			uploadAnnotationInfo450k(); // sito: stringa valori
			String site = fields[4];
			String line = annotationMap450k.get(site);
			line = line+"\tendofline";
			String[] arrayAnnotation = line.split("\t");
			if(site.equals(fields[4])){
				for(int i=0; i<fields.length;i++){
					int leng = arrayAnnotation.length;
					if( leng > i){
						if(i>=5){
							if(arrayAnnotation.length +1 > i){
								if(!fields[i+1].equals(arrayAnnotation[i])){
									System.err.println("site: "+site+" ");
									filecorrupted = true;
								}
							}
						}else{
							if(!fields[i].equals(arrayAnnotation[i])){
								System.err.println("site: "+site+" ");
								filecorrupted = true;
							}
						}
					}
				}				
			}
		}else{
			uploadAnnotationInfo27k(); // sito: stringa valori
			String site = fields[4];
			String line = annotationMap27k.get(site);
			line = line+"\tendofline";
			String[] arrayAnnotation = line.split("\t");
			if(site.equals(fields[4])){
				for(int i=0; i<fields.length;i++){
					int leng = arrayAnnotation.length;
					if( leng > i){
						if(i>=5){
							if(arrayAnnotation.length +1 > i){
								if(!fields[i+1].equals(arrayAnnotation[i])){
									System.err.println("site: "+site+" ");
									filecorrupted = true;
								}
							}
						}else{
							if(!fields[i].equals(arrayAnnotation[i])){
								System.err.println("site: "+site+" ");
								filecorrupted = true;
							}
						}
					}
				}
			}

		}
		return filecorrupted;
	}

	private static void uploadAnnotationInfo450k() {
		if(annotationMap450k.isEmpty() || count450 == 0){
			try{
				BufferedReader file = new BufferedReader(new FileReader(annotation_file_450k_path));
				String line;
				while ((line = file.readLine()) != null) {
					count450 = count450 +1;
					String[] linesplit = line.split("\t");
					annotationMap450k.put(linesplit[4], line);
				}
			}catch(Exception e){

			}
		}
	}
	private static void uploadAnnotationInfo27k() {
		if(annotationMap27k.isEmpty() || count27 == 0){
			try{
				BufferedReader file = new BufferedReader(new FileReader(annotation_file_27k_path));
				String line;
				while ((line = file.readLine()) != null) {
					count27 = count27 +1;
					String[] linesplit = line.split("\t");
					annotationMap27k.put(linesplit[4], line);
				}
			}catch(Exception e){

			}
		}
	}

	//	private static int numberOfLines450k() {
	//		if(count450==0){
	//			try{
	//				BufferedReader file = new BufferedReader(new FileReader("/FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation450/annotation_450k.bed"));
	//				String line;
	//				while ((line = file.readLine()) != null) {
	//					count450 = count450 +1;
	//
	//				}
	//			}catch(Exception e){
	//
	//			}
	//		}
	//		return count450;
	//	}
	//
	//	private static int numberOfLines27k() {
	//		if(count27==0){
	//			try{
	//				BufferedReader file = new BufferedReader(new FileReader("/FTP/ftp-root/opengdc/bed/_annotations/HumanMethylation27/annotation_27k.bed"));
	//				String line;
	//				while ((line = file.readLine()) != null) {
	//					count27 = count27 +1;
	//				}
	//			}catch(Exception e){
	//
	//			}
	//		}
	//		return count27;
	//	}

	//	public static void controllLineNumber(String tumor_experiment){
	//		try {
	//			numberOfLines27k();
	//			numberOfLines450k();
	//			// input the file content to the StringBuffer "input"
	//			if(tumor_experiment.contains("methylation_beta_value")){
	//				File experimentfolder = new File(tumor_experiment);
	//				if(experimentfolder.exists()){
	//					System.out.println(experimentfolder.getAbsolutePath());
	//					for(File fileExperiment : experimentfolder.listFiles()){
	//						if(fileExperiment.getName().endsWith("bed")){
	//							//System.out.println(fileTumor.getAbsolutePath());
	//							BufferedReader file = new BufferedReader(new FileReader(fileExperiment));
	//							String line;
	//							//Files.write((new File("/Users/eleonora/Desktop/045d2f0e-5a4c-4246-95da-d6e4429d9c49-mbv.tmp")).toPath(), "".getBytes("UTF-8"), StandardOpenOption.CREATE);
	//							int count = 0;
	//							while ((line = file.readLine()) != null) {
	//								count = count +1;
	//							}
	//							if(fileExperiment.length() > 50000000){
	//								if(count450<count){
	//									System.err.println("err in file: "+fileExperiment.getAbsolutePath()+" ");
	//
	//								}
	//							}else{
	//								if(count27<count){
	//									System.err.println("err in file: "+fileExperiment.getAbsolutePath()+" ");
	//
	//								}
	//							}
	//
	//						}
	//					}
	//				}
	//			}
	//		}catch(Exception e){}
	//	}


	//CONTROLLO DEI SITI DELL'ANNOTATION CON UN FILE DI INPUT

	private static void annotation450k2inputfile() {
		LinkedHashMap<String, String> mappaInputFile = new LinkedHashMap<String, String>();
		if(annotationMap450k.isEmpty() ){
			try{
				BufferedReader file = new BufferedReader(new FileReader("/Users/eleonora/Documents/humanMethylation27_annotations.bed"));
				String line;
				while ((line = file.readLine()) != null) {
					String[] linesplit = line.split("\t");
					if(!annotationMap450k.containsKey(linesplit[4]))
						annotationMap450k.put(linesplit[4], line);
					else System.err.println("ERROR duplicate site: "+linesplit[4]);
				}
			}catch(Exception e){

			}

			try{
				BufferedReader file = new BufferedReader(new FileReader("/Users/eleonora/Desktop/995c114b-5212-4a13-8547-f887c7f39e69_jhu-usc.edu_BRCA.HumanMethylation27.3.lvl-3.TCGA-BH-A0DE-01A-11D-A112-05.gdc_hg38.txt"));
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
				if(!annotationMap450k.containsKey(site)){
					if(mappaInputFile.get(site).split("\t")[1].equals("NA") || mappaInputFile.get(site).split("\t")[2].equals("*"))
						//System.err.println("OK te site "+site+" has no chr or beta_value");
						conta_siti_esclusi ++; 
					else{
						System.err.println("ERROR site "+site+" not in annotation");
						System.err.println("\tline: "+ mappaInputFile.get(site));}
				}
			}
			System.out.println("x -- > #site in input "+mappaInputFile.size()+"\ny -- > #site in annotation "+annotationMap450k.size()+"\nz -- > #site with no 'chr' or 'beta value' "+conta_siti_esclusi);
			int sum = annotationMap450k.size() +conta_siti_esclusi;
			System.out.println("y + z = "+ sum);
			if(sum == mappaInputFile.size()){
				System.out.println("OK, the annotation file is correct");
			}else
				System.out.println("NO, the annotation file is not correct");


		}
	}






}