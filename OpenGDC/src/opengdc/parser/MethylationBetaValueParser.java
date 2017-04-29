/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.GUI;
import opengdc.resources.GeneNames;
import opengdc.resources.NCBI;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class MethylationBetaValueParser extends BioParser {

	@Override
	public int convert(String program, String disease, String dataType, String inPath, String outPath) {
		int acceptedFiles = FSUtils.acceptedFilesInFolder(inPath, getAcceptedInputFileFormats());
		System.err.println("Data Amount: " + acceptedFiles + " files" + "\n\n");
		GUI.appendLog("Data Amount: " + acceptedFiles + " files" + "\n\n");

		if (acceptedFiles == 0)
			return 1;

		HashSet<String> filesPathConverted = new HashSet<>();

		File[] files = (new File(inPath)).listFiles();
		for (File f: files) {
			if (f.isFile()) {
				String extension = FSUtils.getFileExtension(f);
				if (getAcceptedInputFileFormats().contains(extension)) {
					System.err.println("Processing " + f.getName());
					GUI.appendLog("Processing " + f.getName() + "\n");

					String file_uuid = f.getName().split("_")[0];
					String aliquot_uuid = GDCQuery.retrieveAliquotFromFileUUID(file_uuid);
					if (!aliquot_uuid.trim().equals("")) {
						try {
							Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);

							InputStream fstream = new FileInputStream(f.getAbsolutePath());
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(new InputStreamReader(in));
							String line;
							boolean firstLine = true;
							while ((line = br.readLine()) != null) {
								if (firstLine)
									firstLine = false; // just skip the first line (header)
								else {
									String[] line_split = line.split("\t");
									String chr = line_split[2];
									String start = line_split[3];
									String end = line_split[4];
									String gene_symbol = line_split[5];
									if(gene_symbol.contains(";")){

										gene_symbol = gene_symbol.split(";")[0];
									}
									String entrez_id = getEntrez(gene_symbol);
									String strand = "";
									if(entrez_id!=null && !entrez_id.equals("")){

										strand = getStrand(gene_symbol,entrez_id,start, end,chr); //retrieve strand from NCBI
									}
									if(strand.equals(""))
										strand = "*";
									if(entrez_id==null || entrez_id.equals("")) {	
										NCBI ncbi = NCBI.getInstance(); //inizializza il file data.txt e la mappa con tutte le info che ci sono dentro il file (se non è vuoto)
										String archivePath = ncbi.getNcbiArchive()+"data.txt";
										entrez_id= null;
										ncbi.updateNCBIinfo(archivePath, gene_symbol,start,end,chr,entrez_id,strand);
										}
									String composite_element_ref = line_split[0];
									String beta_value = line_split[1];
									String gene_type = line_split[6];
									String transcript_id = line_split[7];
									String position_to_tss = line_split[8];
									String cgi_coordinate = line_split[9];
									String feature_type = line_split[10];

									ArrayList<String> values = new ArrayList<>();
									values.add(chr);
									values.add(start);
									values.add(end);
									values.add(strand);
									values.add(composite_element_ref);
									values.add(beta_value);
									values.add(gene_symbol);
									values.add(entrez_id);
									values.add(gene_type);
									values.add(transcript_id);
									values.add(position_to_tss);
									values.add(cgi_coordinate);
									values.add(feature_type);

									Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
								}
							}
							br.close();
							in.close();
							fstream.close();

							Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
							filesPathConverted.add(outPath + file_uuid + "." + this.getFormat());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						System.err.println("ERROR: an error has occurred while retrieving the aliquot UUID for :" + file_uuid);
						GUI.appendLog("ERROR: an error has occurred while retrieving the aliquot UUID for :" + file_uuid);
					}
				}
			}
		}

		if (!filesPathConverted.isEmpty()) {
			// write header.schema
			try {
				System.err.println("\n" + "Generating header.schema");
				GUI.appendLog("\n" + "Generating header.schema" + "\n");
				Files.write((new File(outPath + "header.schema")).toPath(), (FormatUtils.generateDataSchema(this.getHeader(), this.getAttributesType())).getBytes("UTF-8"), StandardOpenOption.CREATE);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}



	public static String getStrand(String gene_symbol, String entrez_id, String start, String end,String chr){
		String strand = null;


		NCBI ncbi = NCBI.getInstance(); //inizializza il file data.txt e la mappa con tutte le info che ci sono dentro il file (se non è vuoto)
		String archivePath = ncbi.getNcbiArchive()+"data.txt";
		try {
			//cerca nel file data.txt lo strand
			strand = ncbi.getNCBIinfo("STRAND", entrez_id,start,end);

			//se non trovo lo strand in data.txt allora richiamo ncbi fetch
			if(strand==null){

				//entrez_id = getEntrez(gene_symbol);
				if(	!entrez_id.equals("") && entrez_id!=null){
					strand = ncbi.simpleRetrieveStrand(entrez_id);
				}
				if(strand==null || strand.equals("")){
					strand = "*";
				}
				ncbi.updateNCBIinfo(archivePath, gene_symbol,start,end,chr,entrez_id,strand);



			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//}

		return strand;
	}



	private static String getEntrez(String gene_symbol) throws IOException {
		String entrez_id = null;
		NCBI ncbi = NCBI.getInstance();
		for(HashMap<String,String> val: ncbi.getInfoMap().values()){

			if(gene_symbol.equals(val.get("SYMBOL"))){
				entrez_id=val.get("ENTREZ");
				break;

			}
		}
		if(entrez_id==null){
			if(!gene_symbol.equals(".") && gene_symbol!=null && !gene_symbol.equals("")){
				entrez_id = GeneNames.retriveEntrez_idFromGene_symbol(gene_symbol);
			}
		}

		return entrez_id;
	}


	@Override
	public String[] getHeader() {
		String[] header = new String[13];
		header[0] = "chr";
		header[1] = "start";
		header[2] = "stop";
		header[3] = "strand";
		header[4] = "composite_element_ref";
		header[5] = "beta_value";
		header[6] = "gene_symbol";
		header[7] = "entrez_id";
		header[8] = "gene_type";
		header[9] = "transcript_id";
		header[10] = "position_to_tss";
		header[11] = "cgi_coordinate";
		header[12] = "feature_type";
		return header;
	}

	@Override
	public String[] getAttributesType() {
		String[] attr_type = new String[13];
		attr_type[0] = "STRING";
		attr_type[1] = "LONG";
		attr_type[2] = "LONG";
		attr_type[3] = "CHAR";
		attr_type[4] = "STRING";
		attr_type[5] = "FLOAT";
		attr_type[6] = "STRING";
		attr_type[7] = "STRING";
		attr_type[8] = "STRING";
		attr_type[9] = "STRING";
		attr_type[10] = "STRING";
		attr_type[11] = "STRING";
		attr_type[12] = "STRING";
		return attr_type;
	}

	@Override
	public void initAcceptedInputFileFormats() {
		this.acceptedInputFileFormats = new HashSet<>();
		this.acceptedInputFileFormats.add(".txt");
	}

}
