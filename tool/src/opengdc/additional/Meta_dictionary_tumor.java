package opengdc.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;


public class Meta_dictionary_tumor {

	private static ArrayList<String> experiments = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	static String input= "";
	//static String output= "";
	static String union="";
	static String single ="";
	static String tumor = "";
	static 	TreeMap<String, ArrayList<String>> map = new TreeMap<String, ArrayList<String>>();

	public static void main(String[] args)  {

		input = args[0];
		tumor= input.split("/")[input.split("/").length-1].split("-")[1];

		File unione = new File(input+"/union");
		unione.mkdir();
		union = unione.getPath();// args[2];
		initExperiment();
		String path_union_exp_file = "";
		for(String exp: experiments){
			try {
				path_union_exp_file = unionSingleExpFile(input+"/"+exp);
				if(path_union_exp_file!= null){
					finalExpfile(path_union_exp_file);
				}
			} catch (IOException e) {
			}
		}
		try {
			FileUtils.deleteDirectory(new File(union));

			creaFile();
		} catch (IOException e) {
		}
	}

	public static void initExperiment() {
		if(experiments.isEmpty()){
			experiments.add("gene_expression_quantification");
			experiments.add("isoform_expression_quantification");
			experiments.add("mirna_expression_quantification");
			experiments.add("copy_number_segment");
			experiments.add("masked_copy_number_segment");
			experiments.add("masked_somatic_mutation");
			experiments.add("methylation_beta_value");
		}
	}

	public static String unionSingleExpFile(String input_folder) {
		try {
			File folder = new File(input_folder);
			if(folder.exists()){
				File[] listOfFiles = folder.listFiles();
				String path_union_exp_file = union+"/"+folder.getName()+".txt";
				FileWriter fileWriter;

				fileWriter = new FileWriter(path_union_exp_file);
				for (File file : listOfFiles) {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (file.isFile() && file.getName().endsWith("meta")) {
						String rigaBarcode= null;
						while ((rigaBarcode = br.readLine()) != null ){
							fileWriter.append(rigaBarcode);
							fileWriter.append("\n"); 
						}
					}     
					br.close();
				}
				fileWriter.flush();
				fileWriter.close();
				return folder.getName();
			}else return null;
		} catch (Exception e) {
			return null;		}
	}

	public static void finalExpfile(String folder) throws IOException{
		File file = new File( union+"/"+folder+".txt");
		//		PigServer pigServer = new PigServer(ExecType.LOCAL);
		//		pigServer.registerQuery("docs = LOAD '"+bed_union+"' USING PigStorage('	') as (cluster_id:chararray, terms:chararray) ;");
		//		pigServer.registerQuery("Y1 = foreach docs generate cluster_id;");
		//		pigServer.registerQuery("by_clusters = GROUP Y1 by (cluster_id);by_clusters_count = FOREACH by_clusters GENERATE FLATTEN(group) as (cluster_id), COUNT($1);");
		//		pigServer.store("by_clusters_count", "/Users/eleonora/Desktop/meta2dataType_single/"+folder);

		if(file.getName().contains("txt")){

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line= null;
			while ((line = br.readLine()) != null ){
				String attribute= line.split("\t")[0];
				String value = "";
				try{
					value = line.split("\t")[1];
				}catch(Exception e){
					System.out.println("error");
				}
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
	}

	public static void creaFile() throws IOException{
		String path_single_exp = input+"/meta_dictionary_"+tumor+".txt";
		FileWriter fileWriter = new FileWriter(path_single_exp);
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
	}

//	public static void listFilesForFolder(File folder) {
//		for (final File fileEntry : folder.listFiles()) {
//			if (fileEntry.isDirectory()) {
//				listFilesForFolder(fileEntry);
//			} else {
//				if(fileEntry.getName().equals("part-r-00000")){
//					String u = fileEntry.getParent().split("/")[fileEntry.getParent().split("/").length-2];
//					int j = fileEntry.getParent().indexOf(u)+u.length();
//					String path_meta2dataType_single = fileEntry.getParent().substring(0,j);
//					String filenome = path_meta2dataType_single+"/"+fileEntry.getParent().split("/")[fileEntry.getParent().split("/").length-1]+".txt";
//					File tmp = new File(filenome);
//					fileEntry.renameTo(tmp);
//				}
//			}
//		}
//	}
}