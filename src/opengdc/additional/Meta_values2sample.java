package opengdc.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;


public class Meta_values2sample {

	private static ArrayList<String> experiments = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	static String input= "";
	static String output= "";
	static String union="";
	static String single ="";
	public static void main(String[] args)  {

		input = /*"/Users/eleonora/Downloads/tcga-acc"; */ args[0];

		File unione = new File(input+"/union");
		unione.mkdir();
		union = unione.getPath();

		File singole = new File(input+"/single");
		singole.mkdir();
		single = singole.getPath();

		initExperiment();
		String path_union_exp_file = "";
		for(String exp: experiments){
			try {
				path_union_exp_file = unionSingleExpFile(input+"/"+exp);

				finalExpfile(path_union_exp_file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			experiments.add("mirna_expression_quantification");
			experiments.add("isoform_expression_quantification");
			experiments.add("masked_copy_number_segment");
			experiments.add("masked_somatic_mutation");
			experiments.add("methylation_beta_value");
			experiments.add("copy_number_segment");

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

	public static void finalExpfile(String folder) throws IOException{
		File file = new File( union+"/"+folder+".txt");
		//		PigServer pigServer = new PigServer(ExecType.LOCAL);
		//		pigServer.registerQuery("docs = LOAD '"+bed_union+"' USING PigStorage('	') as (cluster_id:chararray, terms:chararray) ;");
		//		pigServer.registerQuery("Y1 = foreach docs generate cluster_id;");
		//		pigServer.registerQuery("by_clusters = GROUP Y1 by (cluster_id);by_clusters_count = FOREACH by_clusters GENERATE FLATTEN(group) as (cluster_id), COUNT($1);");
		//		pigServer.store("by_clusters_count", "/Users/eleonora/Desktop/meta2dataType_single/"+folder);

		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		String path_single_exp = single+"/"+folder+".txt";
		FileWriter fileWriter = new FileWriter(path_single_exp);
		if(file.getName().contains("txt")){

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line= null;
			while ((line = br.readLine()) != null ){
				String attribute= line.split("\t")[0];
				String value = line.split("\t")[1];
				String value2data = attribute+"|"+value;
				if(map.containsKey(value2data)){
					int count = map.get(value2data);
					map.put(value2data, count+1);
				}else map.put(value2data, 1);
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
		FileWriter fileWriter = new FileWriter(input+"/meta_values2sample.tsv") ;
		//lista di "attributo|valore"
		ArrayList<String> listaAll = new ArrayList<String>();
		TreeMap<String,Integer> attributevalue2count = new TreeMap<String,Integer>();
		for (File file : listOfFiles) { //metti nella lista tutti gli attributicon valore di tutti gli esperimenti
			if(file.getName().contains("txt")){

				BufferedReader br = new BufferedReader(new FileReader(file));
				String line= null;
				while ((line = br.readLine()) != null ){
					String attribute2value= line.split("\t")[0];
					String count= line.split("\t")[1];
					listaAll.add(attribute2value+"\t"+count);
				}
				br.close();
			}
		}
		// conta e metti nella mappa gli attributi con valore che ci sono
		for(String attribute2value : listaAll){
			String a2v= attribute2value.split("\t")[0];
			int count= Integer.valueOf(attribute2value.split("\t")[1]);
			if(attributevalue2count.containsKey(a2v)){
				int c = attributevalue2count.get(a2v);
				attributevalue2count.put(a2v, c+count);
			}else 	attributevalue2count.put(a2v, count);

		}
		//stampa attributo2value e count
		for(String attribute2value : attributevalue2count.keySet()){
			fileWriter.append(attribute2value+"\t"+attributevalue2count.get(attribute2value));
			fileWriter.append("\n");
		}
		//Collections.sort((List<String>)attribute_value_init.keySet());

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