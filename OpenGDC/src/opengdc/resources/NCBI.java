package opengdc.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import opengdc.util.QueryParser;

public class NCBI {

	private static String NCBI_ARCHIVE = null;
	private static HashMap<String, HashMap<String, String>> infoMap = null;

	private static NCBI istanza;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static NCBI getInstance()
	{
		if (istanza == null)
		{
			istanza = new NCBI();
		}

		return istanza; 
	}

	public NCBI(){
		this.NCBI_ARCHIVE = "/Users/mac/workspace/GDC/package/appdata/ncbi_archieve/";
		this.infoMap = getAllNCBIinfo();

	}
	public static HashMap<String, HashMap<String, String>> getInfoMap() {
		return infoMap;
	}

	public static void setInfoMap(HashMap<String, HashMap<String, String>> infoMap1) {
		infoMap = infoMap1;
	}

	public static String getNcbiArchive() {
		return NCBI_ARCHIVE;
	}

	public static void setNcbiArchive(String namearchive) {
		NCBI_ARCHIVE = namearchive;
	}

	public static String simpleRetrieveStrand(String entrez) {
		try {

			String strand = "";


			String ncbiBiotabQuery = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&amp;id=" + entrez;
			File ncbiBiotab_tmp = File.createTempFile("efetch.fcgi", "txt");
			QueryParser.downloadDataFromUrl(ncbiBiotabQuery, ncbiBiotab_tmp.getAbsolutePath(), 0);
			BufferedReader reader = new BufferedReader(new FileReader(ncbiBiotab_tmp.getAbsolutePath()));
			String line = reader.readLine();

			String genomeVersion = "grch38";
			boolean genomeMatch = false; // match on GRCh37 genome version




			while (line != null) {
				/////////////////////////////////////////////////
				if (line.trim().toLowerCase().contains("heading") && line.toLowerCase().contains(genomeVersion))
					genomeMatch = true;
				/////////////////////////////////////////////////


				if (genomeMatch) {
					if (line.trim().toLowerCase().contains("strand")) {
						if (strand.equals("")) {
							String[] lineSplit = line.split(" ");
							String lastString = lineSplit[lineSplit.length-1];
							String strand_ = lastString.substring(0, lastString.length()-1);
							if (strand_.trim().toLowerCase().equals("minus"))
								strand = "-";
							else if (strand_.trim().toLowerCase().equals("plus"))
								strand = "+";
							//System.err.println("--- strand: " + strand);
						}
					}
				}
				line = reader.readLine();
			}
			reader.close();
			ncbiBiotab_tmp.delete();

			//System.err.println();

			return strand;
		} catch (IOException e) {
			e.printStackTrace();
			return "Error NCBI.simpleRetrieveStrand";
		}

	}

	public static HashMap<String, HashMap<String, String>> getAllNCBIinfo() {
		HashMap<String, HashMap<String, String>> result = new HashMap<>();
		try {
			String archivePath = NCBI_ARCHIVE+ "/" +"data.txt";
			File fileData = new File(archivePath);

			if (!fileData.exists()) {

				fileData.createNewFile();

			}
			InputStream fstream = new FileInputStream(archivePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] arr = line.split("\t");
					//if(arr.length!=6) System.out.println("STOP--------------------------------------"+line);
					HashMap<String, String> info = new HashMap<>();
					info.put("STRAND", arr[2]);
//					info.put("START", arr[3]);
//					info.put("END", arr[4]);
//					info.put("CHR", arr[2]);
					info.put("SYMBOL", arr[1]);
					String entrez = arr[0];
					if (arr[0].equals("null")){
						//arr[0]= "null";
						entrez = arr[0]+"_"+arr[1];

					}
					info.put("ENTREZ", arr[0]);
					//String entrez = arr[0]+"_"+arr[3]+"_"+arr[4];
					result.put(entrez, info);
				}
			}
			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getNCBIinfo(String field, String entrez_id, String gene_symbol) throws IOException{
		String info = null;
		//HashMap<String, HashMap<String, String>> mappa = NCBI.getAllNCBIinfo();
		for(String entrez: infoMap.keySet()){
			if(entrez_id.equals("null")){
				if(entrez_id.equals(entrez.split("_")[0]) && gene_symbol.equals(entrez.split("_")[1])){
					info = infoMap.get(entrez).get(field);
					break;
				}
		}else{
			if(entrez_id.equals(entrez)){
				info = infoMap.get(entrez).get(field);
				break;
			}
		}

		}
		return info;
	}



	public static void updateNCBIinfo(String archive, String gene_symbol, String entrez_id, String strand) {
		File resFile = new File(archive);
		//if(!infoMap.containsKey(entrez_id+"_"+start+"_"+end)){
			if(!infoMap.containsKey(entrez_id) && !infoMap.containsKey("null_"+gene_symbol)){

			if (resFile.exists()) {
				try {
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resFile, true), "UTF-8"));
			//		output.append(entrez_id + "\t" + gene_symbol + "\t" + chr + "\t" + start + "\t" + end + "\t" + strand);
					output.append(entrez_id + "\t" + gene_symbol + "\t" + strand);
					output.newLine();
					output.close();
					HashMap<String, String> info = new HashMap<>();

					info.put("STRAND", strand);
//					info.put("START", start);
//					info.put("END", end);
//					info.put("CHR", chr);
					info.put("SYMBOL", gene_symbol);
					info.put("ENTREZ", entrez_id);
					String entrez=entrez_id;
					if(entrez_id.equals("null"))
						entrez = entrez_id+"_"+gene_symbol;
					//infoMap.put(entrez+"_"+start+"_"+end, info);
					infoMap.put(entrez, info);


				} catch (Exception e) {
					System.err.print(e);
				}	
			}
		}
	}

}
