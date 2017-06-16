/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.integration;

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
import opengdc.Settings;
import opengdc.util.DownloadUtils;

/**
 *
 * @author fabio
 */
public class NCBI {

	private static final String GENOME_VERSION = "grch38";
	private static HashMap<String, HashMap<String, String>> ncbi_data = new HashMap<>();
	private static String ncbi_table_path = Settings.getNCBIDataPath_local();

	public static boolean updateNCBIData(String gdc_entrez, String gdc_gene, String chr, String start, String end, String strand) {
		File ncbidata = new File(ncbi_table_path);
		if (ncbidata.exists()) {
			try {
				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ncbidata.getAbsolutePath(), true), "UTF-8"));
				output.append(gdc_entrez + "\t" + gdc_gene + "\t" + chr + "\t" + start + "\t" + end + "\t" + strand);
				output.newLine();
				output.close();

				//loadNCBIData(true);
				HashMap<String, String> info = new HashMap<>();
				info.put("STRAND", strand);
				info.put("START", start);
				info.put("END", end);
				info.put("CHR", chr);
				info.put("GDC_SYMBOL", gdc_gene);
				info.put("GDC_ENTREZ", gdc_entrez);
				ncbi_data.put(gdc_gene, info);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public static HashMap<String, HashMap<String, String>> loadNCBIData(boolean forceLoad) {
		if (ncbi_data.isEmpty() || forceLoad) {
			ncbi_data = new HashMap<>();
			try {
				InputStream fstream = new FileInputStream(ncbi_table_path);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						String[] arr = line.split("\t");
						HashMap<String, String> info = new HashMap<>();
						info.put("STRAND", arr[5]);
						info.put("START", arr[3]);
						info.put("END", arr[4]);
						info.put("CHR", arr[2]);
						info.put("GDC_SYMBOL", arr[1]);
						info.put("GDC_ENTREZ", arr[0]);
						String gdc_symbol = arr[1];
						ncbi_data.put(gdc_symbol, info);
					}
				}
				br.close();
				in.close();
				fstream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ncbi_data;
	}

	public static HashMap<String, String> getGeneInfo(String symbol) {
		HashMap<String, HashMap<String, String>> data = loadNCBIData(false);
		if (data.containsKey(symbol))
			return data.get(symbol);
		
		return null;
	}

	public static HashMap<String, String> retrieveGenomicCoordinates(String entrez, String gene) {
		try {
			HashMap<String, String> result = new HashMap<>();
			HashMap<String, String> result_tmp = new HashMap<String, String>();

			String strand = "";
			String chr = "";
			int start = -1;
			int end = -1;

			//String ncbiBiotabQuery = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez + "&format=biotab";
			String ncbiBiotabQuery = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&amp;id=" + entrez;
			File ncbiBiotab_tmp = new File(Settings.getTmpDir()+"ncbi.efetch.fcgi.txt"); 
			//File.createTempFile("efetch.fcgi", "txt");
			DownloadUtils.downloadDataFromUrl(ncbiBiotabQuery, ncbiBiotab_tmp.getAbsolutePath(), 0);
			BufferedReader reader = new BufferedReader(new FileReader(ncbiBiotab_tmp.getAbsolutePath()));
			String line = reader.readLine();

			boolean genomeMatch = false; // match on GRCh38 genome version

			boolean replaced = false;
			boolean db_GeneID = false;

			String newGene = "";
			String newEntrez = "";
			while (line != null) {
				/////////////////////////////////////////////////
				if (line.trim().toLowerCase().contains("heading") && line.toLowerCase().contains(GENOME_VERSION))
					genomeMatch = true;
				/////////////////////////////////////////////////
				if (line.trim().toLowerCase().contains("current-id")) {
					replaced = true;
				}
				if (replaced) {
					if (line.trim().toLowerCase().contains("db \"geneid\"")) {
						db_GeneID = true;
					}
					if (db_GeneID) {
						if (line.trim().toLowerCase().contains("tag id")) {
							String[] line_split = line.split(" ");
							newEntrez = line_split[line_split.length-1];
							result_tmp = retrieveGenomicCoordinates(newEntrez, gene);
							result_tmp.put("GDC_ENTREZ", newEntrez);
							break;
						}
					}
				}
				////////////////////////////////////////////////
				if (line.trim().toLowerCase().contains("locus")) {
					if (newGene.equals("")) {
						String[] lineSplit = line.split(" ");
						String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
						newGene = lastString.substring(0, lastString.length()-1);
						//System.err.println("--- newGene: " + newGene);
					}
				}
				else if (line.trim().toLowerCase().contains("geneid")) {
					if (newEntrez.equals("")) {
						String[] lineSplit = line.split(" ");
						String lastString = lineSplit[lineSplit.length-1];
						newEntrez = lastString.substring(0, lastString.length()-1);
						//System.err.println("--- newEntrez: " + newEntrez);
					}
				}

				if (genomeMatch) {
					if (line.trim().toLowerCase().contains("label") && line.trim().toLowerCase().contains("chromosome")) {
						if (chr.equals("")) {
							String[] lineSplit = line.split(" ");
							int i;
							for(i= 0; i< lineSplit.length;i++){
								String t = lineSplit[i].trim().toLowerCase();
								if(t.equals("\"chromosome")){
									chr = lineSplit[i+1];
									break;
								}
							}
							//String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
							//chr = lastString.substring(0, lastString.length()-1);
							// System.err.println("--- chr: " + chr);
						}
					}
					else if (line.trim().toLowerCase().contains("from")) {
						if (start == -1) {
							String[] lineSplit = line.split(" ");
							String lastString = lineSplit[lineSplit.length-1];
							start = Integer.valueOf(lastString.substring(0, lastString.length()-1));
							//System.err.println("--- start: " + start);
						}
					}
					else if (line.trim().toLowerCase().contains("to")) {
						if (end == -1) {
							String[] lineSplit = line.split(" ");
							String lastString = lineSplit[lineSplit.length-1];
							end = Integer.valueOf(lastString.substring(0, lastString.length()-1));
							//System.err.println("--- end: " + end);
						}
					}
					else if (line.trim().toLowerCase().contains("strand")) {
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

			if (!result_tmp.isEmpty())
				return result_tmp;

			if (!chr.equals("") && (start > 0 && end > 0)) {
				result.put("CHR", chr);
				result.put("START", String.valueOf(start+1)); // +1 : NCBI is 0-based
				result.put("END", String.valueOf(end+1)); // +1 : NCBI is 0-based
				result.put("STRAND", strand);
				result.put("GDC_ENTREZ", entrez);
				result.put("GDC_SYMBOL", gene);
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}


	public static HashMap<String, String> extractNCBIinfo(String chr, String gene_symbol_comp,
			String start_site, String end_site, String gene_type_comp, String transcript_id_comp, String position_to_tss_comp ) {
		String transcript = ""; 
		String position_to_TSS = ""; 
		String gene_type = "";
		String gene_symbol = "";
		String strand ="";
		String entrez = "";
		String arr = null;
		boolean trovato = false;
		HashMap<String, String> result = new HashMap<>();
		String[] genes = gene_symbol_comp.split(";");
		int i = 0;
		while(i<genes.length && !trovato){
			String gene_symbol_tmp = genes[i];
			int last = getLastIndex(genes,i);
			boolean unico_gene = false;
			if(i==0 && last==genes.length){
				unico_gene = true;
				trovato=true;
			}
			//cerca nel file piccolo
			HashMap<String, HashMap<String, String>> data = loadNCBIData(false);
			if (data.containsKey(gene_symbol_tmp)){
				String start = data.get(gene_symbol_tmp).get("START");
				String end = data.get(gene_symbol_tmp).get("END");
				int startf = Integer.parseInt(start);
				int s_site = Integer.parseInt(start_site);
				int endf = Integer.parseInt(end);
				int e_site = Integer.parseInt(end_site);

				if(s_site>= startf && endf>=e_site){
					trovato = true;
				}
				strand = data.get(gene_symbol_tmp).get("STRAND");
				entrez = data.get(gene_symbol_tmp).get("GDC_ENTREZ");

			}else{
				arr = isInCpGsite(chr, gene_symbol_tmp, start_site, end_site, unico_gene); //vedo se il gene è in CpG site
				if(arr!=null){
					if(!unico_gene){

						trovato = true;
					}
					strand =arr.split("\t")[6];


					entrez = getEntrezFromNCBIline(arr.split("\t")[8]);
				}
			}
			if(trovato){
				for(int z =i;z<last;z++){
					transcript = (transcript+";" + transcript_id_comp.split(";")[z]);
					position_to_TSS = (position_to_TSS +";"+ position_to_tss_comp.split(";")[z]);
				}
			}

			//				if(i==0 && last==genes.length){ //se ho un solo gene
			//					unico_gene = true;
			//
			//				}
			//				arr = isInCpGsite(gene_symbol_tmp, start_site, end_site, unico_gene); //vedo se il gene è in CpG site
			//				if(arr!=null){//se arr non è vuoto ho l'intera riga se è ne CpG site, oppure ho lo strand se è l'unico gene
			//					trovato = true;
			//					for(int z =i;z<last;z++){
			//						if(unico_gene){
			//							transcript =";" + transcript_id_comp;
			//							position_to_TSS =";" + position_to_tss_comp;
			//							strand =arr;
			//							break; //esco dal ciclo perchè è l'unico gene 
			//						}else{
			//							transcript = (transcript+";" + transcript_id_comp.split(";")[z]);
			//							position_to_TSS = (position_to_TSS +";"+ position_to_tss_comp.split(";")[z]);
			//							strand =arr.split("\t")[6];
			//
			//						}
			//					}
			gene_symbol = gene_symbol_tmp;

			gene_type = gene_type_comp.split(";")[i];
			//} // se arr è null vai al prossimo gene
			i=last;
		}
		result.put("TRANSCRIPT_ID", transcript.substring(1)); 
		result.put("POSITION_TO_TSS", position_to_TSS.substring(1)); 
		result.put("GENE_TYPE", gene_type); 
		result.put("STRAND", strand);
		result.put("SYMBOL", gene_symbol);
		result.put("ENTREZ", entrez);



		return result;
	}

	private static String getEntrezFromNCBIline(String arr) {
		String entrez="";
		for (String data1:arr.split(",")) {
			if (data1.toLowerCase().trim().contains("geneid")) {
				String[] name_split = data1.split(":");
				entrez = name_split[name_split.length-1];
				break;
			}
		}
		return entrez;
	}
	public static String isInCpGsite(String chr, String gene_symbol,
			String start_site, String end_site, boolean unico_gene){
		String a = null;
		try {
			InputStream fstream = new FileInputStream(Settings.getNCBIDataPath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			boolean trovato = false;
			while ((line = br.readLine()) != null && !trovato) {
				try {
					if (!line.startsWith("#")) {
						String[] arr = line.split("\t");
						String extendedInfo = arr[8];
						int start = Integer.parseInt(arr[3]);
						int s_site = Integer.parseInt(start_site);
						int end = Integer.parseInt(arr[4]);
						int e_site = Integer.parseInt(end_site);
						if (extendedInfo.contains("Name=")) {
							String[] extendedInfo_arr = extendedInfo.split(";");
							for (String data: extendedInfo_arr) {
								if (data.toLowerCase().trim().startsWith("name")) {
									String[] name_split = data.split("=");
									String symbol = name_split[name_split.length-1];
									if(gene_symbol.equals(symbol)){
										if((s_site>= start && end>=e_site) || unico_gene){
											String entrez_tmp = getEntrezFromNCBIline(extendedInfo);
											a = line; 
											trovato = true;
											updateNCBIData(entrez_tmp, symbol, chr, arr[3], arr[4], arr[6]);
										}
										
										
									}
								}
							}							
						}

					}
				} catch (Exception e) {}
			}
			br.close();
			in.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  a;


	}

	public static int getLastIndex(String[] genes, int i) {
		int j = i+1;
		while(j< genes.length && genes[j].equals(genes[i])){
			j ++;
		}
		return j;
	}

}
