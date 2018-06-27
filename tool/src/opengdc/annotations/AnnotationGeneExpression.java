package annotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AnnotationGeneExpression {

	static HashMap<Integer, HashMap<Integer, LinkedHashMap<String,String>>> dataMapChrGeneExp = new HashMap<>();

	/**
	 * @param args
	 * @throws  
	 */
	public static void main(String[] args)  {


		File[] listOfFiles = new File("/FTP/ftp-root/opengdc/bed/tcga/").listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				extractAnnotation(listOfFiles[i]+"/gene_expression_quantification");
			}
		}

		try {
			FileWriter fw;
			fw = new FileWriter("/FTP/ftp-root/opengdc/bed/_annotations/GeneExpression/gene_expression_annotations.bed");
			boolean firstline = true;

			if (!dataMapChrGeneExp.isEmpty()) {
				ArrayList<Integer> chrs = new ArrayList<>(dataMapChrGeneExp.keySet());
				// sort by chr
				Collections.sort(chrs);
				for (Integer chr: chrs) {
					HashMap<Integer, LinkedHashMap<String,String>> dataList = dataMapChrGeneExp.get(chr);
					// sort by start position
					ArrayList<Integer> starts = new ArrayList<>(dataList.keySet());
					Collections.sort(starts);
					for (Integer start: starts) {
						LinkedHashMap<String,String> dataArray = dataList.get(start);
						for (String data: dataArray.keySet()) {
							try {
								if (!firstline){
									fw.append("\n");
								}
								firstline = false;
								fw.append(dataArray.get(data));
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}


			fw.flush();
			fw.close();
		} catch (IOException e) {
		}


		//		try {
		//			FileWriter fw;
		//			fw = new FileWriter("/Users/eleonora/Desktop/annotation_27k.bed");
		//			boolean firstline = true;
		//			for(String line: list_distinct_rows_27k){
		//				if (!firstline){
		//					fw.append("\n");
		//				}
		//				firstline = false;
		//				fw.append(line);
		//			}
		//			fw.flush();
		//			fw.close();
		//		} catch (IOException e) {
		//		}
	}

	public static void extractAnnotation(String type){
		File gene_expression_folder = new File(type+"/");
		System.out.println(gene_expression_folder.getAbsolutePath());

		for(File fileTumor : gene_expression_folder.listFiles()){
			long sizeFile = fileTumor.length();
			if(fileTumor.getName().endsWith("bed") ){
				//System.out.println(fileTumor.getAbsolutePath());
				try{
					BufferedReader file = new BufferedReader(new FileReader(fileTumor));
					String line;

					while ((line = file.readLine()) != null) {
						String[] array = line.split("\t");
						String [] arrayvuoto = {"","","","","","","",""};
						for(int i=0; i<arrayvuoto.length;i++){
							if(array.length > i){
								arrayvuoto[i] = array[i];
							}
						}
						String linefinale = "";
						for(String f : arrayvuoto){
							linefinale = linefinale +"\t"+ f;
						}
						line = linefinale.substring(1,linefinale.length());


						String chr = arrayvuoto[0];
						String start = arrayvuoto[1];

						//							list_distinct_rows_450k.add(line);
						//						else
						//							list_distinct_rows_27k.add(line);


						/******************************************************** **************/
						/** populate dataMap then sort genomic coordinates and print entries **/
						int chr_id = Integer.parseInt(chr.replaceAll("chr", "").replaceAll("X", "23").replaceAll("Y", "24").replaceAll("M", "25"));
						int start_id = Integer.parseInt(start);
						HashMap<Integer, LinkedHashMap<String,String>> dataMapStart = new HashMap<>();
						LinkedHashMap<String,String> dataList = new LinkedHashMap<String,String>();
						if (dataMapChrGeneExp.containsKey(chr_id)) {
							dataMapStart = dataMapChrGeneExp.get(chr_id);                                        
							if (dataMapStart.containsKey(start_id))
								dataList = dataMapStart.get(start_id);
							if(!dataList.containsKey(arrayvuoto[4]))
								dataList.put(arrayvuoto[4],line);
						}
						else
							if(!dataList.containsKey(arrayvuoto[4]))
								dataList.put(arrayvuoto[4],line);

						dataMapStart.put(start_id, dataList);
						dataMapChrGeneExp.put(chr_id, dataMapStart);
						/**********************************************************************/
					}
					file.close();

				}catch(Exception e){

				}
			}
		}
	}
}
