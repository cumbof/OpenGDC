package opengdc.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;



public class Meta2disease_table {

	private static ArrayList<String> experiments = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	static String input= "";
	static String output= "";
	static String union="";
	static String single ="";
	public static void main(String[] args) {

		input = /*"/Users/eleonora/Desktop/target/";*/  args[0];

		File unione = new File(input+"/union");
		unione.mkdir();
		union = unione.getPath();

		File singole = new File(input+"/single");
		singole.mkdir();
		single = singole.getPath();

		initExperiment();
		String path_union_exp_file = "";
		File[] tumordirs = new File(input).listFiles();
		for(File tumordir: tumordirs){
			if(tumordir.getName().contains("tcga-")){
				try {
					path_union_exp_file = unionSingleTumorFile(tumordir.getAbsolutePath());
					finalExpfile(path_union_exp_file);
				} catch (Exception e) {

				}
			}
		}
		try {
			creaFile(single+"/");
			FileUtils.deleteDirectory(new File(union));
			FileUtils.deleteDirectory(new File(single));
		} catch (Exception e) {

		}
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

	public static String unionSingleTumorFile(String input_folder) {
		try {
			File folder = new File(input_folder);
			String path_union_exp_file = union+"/"+folder.getName()+".txt";
			FileWriter fileWriter;
			fileWriter = new FileWriter(path_union_exp_file);
			for(String exp : experiments){
				try{
				File esperimento = new File(input_folder+"/"+exp);
				File[] listOfFiles = esperimento.listFiles();
				for (File file : listOfFiles) {
					
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (file.isFile() && file.getName().endsWith("meta")) {
						String rigaBarcode= null;
						while ((rigaBarcode = br.readLine()) != null ){
							
							fileWriter.append(rigaBarcode.split("\t")[0]);
							
							fileWriter.append("\n"); 
						}
					}     
					br.close();
				}
				}catch(Exception e){
					
				}

			}
			fileWriter.flush();
			fileWriter.close();

			return folder.getName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;		}
	}

	public static void finalExpfile(String folder) throws IOException{
		File file = new File( union+"/"+folder+".txt");	
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		String path_single_exp = single+"/"+folder+".txt";
		FileWriter fileWriter = new FileWriter(path_single_exp);
		if(file.getName().contains("txt")){

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line= null;
			while ((line = br.readLine()) != null ){
				String attribute= line.split("\t")[0];
				
				if(!map.containsKey(attribute)){
					map.put(attribute, 1);
				}
			}

			br.close();
		}
		for(String attributo: map.keySet()){
			fileWriter.append(attributo+"\t"+map.get(attributo));
			fileWriter.append("\n");
		}
		fileWriter.flush();
		fileWriter.close();
	}

	public static void creaFile(String nomeFile) throws IOException{
		File folder = new File(nomeFile);
		//listFilesForFolder(folder); //used with pig
		File[] listOfFiles = folder.listFiles();
		//mappa nome esperimento, mappa attributo valore
		TreeMap<String, HashMap<String, String>> map = new TreeMap<String, HashMap<String, String>>();
		FileWriter fileWriter = new FileWriter(input+"/meta2disease_table.csv") ;
		TreeMap<String , String> attribute_value_init = new TreeMap<String , String>();
		for (File file : listOfFiles) {
			if(file.getName().contains("txt")){

				BufferedReader br = new BufferedReader(new FileReader(file));
				String line= null;
				while ((line = br.readLine()) != null ){
					String attribute= line.split("\t")[0];
					attribute_value_init.put(attribute, "0");
				}
				br.close();
			}
		}

		for (File file : listOfFiles) {
			if(file.getName().contains("txt")){
				HashMap<String , String> attrval = new HashMap<String,String>(attribute_value_init);
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line= null;
				while ((line = br.readLine()) != null ){
					String attribute= line.split("\t")[0];
					String value= line.split("\t")[1];
					attrval.put(attribute, value);
				}
				String name = file.getName().substring(0, file.getName().length()-4);
				map.put(name,attrval);
				br.close();
			}
		}

		for(String nameExp : map.keySet())
			fileWriter.append(","+nameExp); //header

		fileWriter.append("\n"); 
		for(String attribute : attribute_value_init.keySet()){
			fileWriter.append(attribute); 

			for(String key : map.keySet()){
				HashMap<String,String> attrval = map.get(key);
				String value;
				if(attrval.containsKey(attribute)){
					value = attrval.get(attribute);
					fileWriter.append(","+value); 
				}
			}
			fileWriter.append("\n"); 
		}
		fileWriter.flush();
		fileWriter.close();
	}

}