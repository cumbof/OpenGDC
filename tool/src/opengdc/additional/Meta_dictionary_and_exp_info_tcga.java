package opengdc.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import opengdc.util.GDCQuery;

import org.apache.commons.io.FileUtils;


public class Meta_dictionary_and_exp_info_tcga {

	private static ArrayList<String> experiments = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	static String input= "";
	static String union="";
	static String single ="";
	public static void main(String[] args) {

		input = /*"/Users/eleonora/Desktop/tcga-kirp";*/ args[0];
		File unione = new File(input+"/union");
		unione.mkdir();
		union = unione.getPath();// args[2];
		initExperiment();
		String path_union_exp_file = "";
		for(String exp: experiments){
			try {
				path_union_exp_file = unionSingleExpFile(input+"/"+exp);

				finalExpfile(path_union_exp_file, input+"/"+exp);
			} catch (Exception e) {
			}
		}
		try {
			FileUtils.deleteDirectory(new File(union));
		} catch (Exception e) {
		}
		//creaFile(single+"/");
	}

	public static void initExperiment() {
		if(experiments.isEmpty()){
			experiments.add("gene_expression_quantification");
			experiments.add("copy_number_segment");
			experiments.add("isoform_expression_quantification");
			experiments.add("masked_copy_number_segment");
			experiments.add("masked_somatic_mutation");
			experiments.add("methylation_beta_value");
			experiments.add("mirna_expression_quantification");
		}
	}

	public static String unionSingleExpFile(String input_folder) throws IOException{
		File folder = new File(input_folder);
		File[] listOfFiles = folder.listFiles();
		String path_union_exp_file = union+"/"+folder.getName()+".txt";
		FileWriter fileWriter = new FileWriter(path_union_exp_file) ;
		for (File file : listOfFiles) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			if (file.isFile() && file.getName().endsWith("meta")) {
				String rigaBarcode= null;
				while ((rigaBarcode = br.readLine()) != null ){
					//					if(!rigaBarcode.startsWith("clinical") && !rigaBarcode.startsWith("biospecimen") && !rigaBarcode.startsWith("manually"))
					//						System.out.println(file.getName());
					
					fileWriter.append(rigaBarcode);
					fileWriter.append("\n"); 
				}
			}     
			br.close();
		}
		fileWriter.flush();
		fileWriter.close();
		return folder.getName();
	}

	public static void finalExpfile(String folder, String folderInput) throws IOException{

		File file = new File( union+"/"+folder+".txt");
		//		PigServer pigServer = new PigServer(ExecType.LOCAL);
		//		pigServer.registerQuery("docs = LOAD '"+bed_union+"' USING PigStorage('	') as (cluster_id:chararray, terms:chararray) ;");
		//		pigServer.registerQuery("Y1 = foreach docs generate cluster_id;");
		//		pigServer.registerQuery("by_clusters = GROUP Y1 by (cluster_id);by_clusters_count = FOREACH by_clusters GENERATE FLATTEN(group) as (cluster_id), COUNT($1);");
		//		pigServer.store("by_clusters_count", "/Users/eleonora/Desktop/meta2dataType_single/"+folder);

		TreeMap<String, ArrayList<String>> map = new TreeMap<String, ArrayList<String>>();
		String path_single_exp = input+"/"+folder+"/meta_dictionary.txt";
		String path_single_exp_info = input+"/"+folder+"/exp_info.tsv";
		FileWriter fileWriter = new FileWriter(path_single_exp);
		FileWriter fileWriterExpInfo = new FileWriter(path_single_exp_info);
		if(file.getName().contains("txt")){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line= null;
			while ((line = br.readLine()) != null ){
				String attribute= line.split("\t")[0];
				String value = line.split("\t")[1].toUpperCase();
				if(map.containsKey(attribute)){
					ArrayList<String> values = map.get(attribute);
					if(!values.contains(value)){
						values.add(value);
						map.put(attribute, values);
					}
				}else {
					ArrayList<String> values = new ArrayList<String>();
					values.add(value);
					map.put(attribute, values);
				}
			}
			br.close();
		}
		//stampa meta_dictionary e exp_info
		int sample = 0;
		int patient = 0;
		int aliquot = 0;

		for(String attributo: map.keySet()){
			fileWriter.append(attributo+"\n");
			Collections.sort(map.get(attributo));
			for(String value: map.get(attributo)){
				fileWriter.append(value+"\n");
			}
			fileWriter.append("\n");
		}
		fileWriter.flush();
		fileWriter.close();

		// biospecimen__shared__bcr_patient_uuid == gdc__case_id
		if(map.containsKey("gdc__case_id")){
			patient = map.get("gdc__case_id").size();
		}
		// biospecimen__bio__bcr_sample_uuid == gdc__samples__sample_id
		if(map.containsKey("gdc__samples__sample_id")){
			sample = map.get("gdc__samples__sample_id").size();
		}
		// biospecimen__bio__bcr_aliquot_uuid == gdc__aliquots__aliquot_id
		if(map.containsKey("gdc__aliquots__aliquot_id")){
			aliquot = map.get("gdc__aliquots__aliquot_id").size();
		}
		fileWriterExpInfo.append("number of aliquots\t"+aliquot+"\n");
		fileWriterExpInfo.append("number of samples\t"+sample+"\n");
		fileWriterExpInfo.append("number of patients\t"+patient);
		fileWriterExpInfo.flush();
		fileWriterExpInfo.close();

	}

}