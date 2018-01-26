package opengdc.integration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import opengdc.Settings;

public class NCBI {

	private static String history_table_path = Settings.getHistoryNCBIDataPath();
	private static String ncbi_table_path = Settings.getNCBIDataPath();
	private static HashMap<String, String> symbol2entrez = new HashMap<>();
	private static HashMap<String, String> deprecatesymbol2entrez = new HashMap<>();
	//	private static HashMap<String, String> mirnaid2entrez = new HashMap<>();

	public static String getEntrezFromSymbol(String symbol_from_gencode){
		HashMap<String, String> data_symbol2entrez = getSymbol2Entrez();
		if (!data_symbol2entrez.isEmpty()) {
			String symbol_lower = symbol_from_gencode.trim().toLowerCase();
			if(data_symbol2entrez.containsKey(symbol_lower)) 
				return data_symbol2entrez.get(symbol_lower);
		}
		HashMap<String, String> data_deprecated_symbol2entrez = getDeprecatedSymbol2Entrez();
		if (!data_deprecated_symbol2entrez.isEmpty()) {
			String symbol_lower = symbol_from_gencode.trim().toLowerCase();
			if(!data_symbol2entrez.containsKey(symbol_lower)){ //se non è già nell'altra mappa, anche se, se lo fosse sarebbe gia tornato l'id
				if(data_deprecated_symbol2entrez.containsKey(symbol_lower)) 
					return data_deprecated_symbol2entrez.get(symbol_lower);
			}
		}

		return null;

	}

	/*qui tiro fuori i geni deprecati che:
	 * - hanno un loro id, e anche un id nuovo ( al quale però corrisponde anche un symbol nuovo);
	 * - hanno un loro id, e non hanno il riferimento a un nuovo id (quindi nuovo symbol)
	 * 
	 * Tutti i geni che non sono in queto file sono ancora usati con lo stesso id, ma in versioni del genoma successive, hanno cambiato simbolo (non id)
	 */

	public static HashMap<String, String> getDeprecatedSymbol2Entrez() {
		if (deprecatesymbol2entrez.isEmpty()) {
			try {
				boolean firstLine = true; // just to skip the first line (header)
				InputStream fstream = new FileInputStream(history_table_path);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					try {
						if (!firstLine) {
							String[] arr = line.split("\t");
							String taxonomy_id = arr[0];
							String old_symbol = arr[3];
							String entrez = arr[2];
							//String new_entrez = arr[1];
							if(taxonomy_id.equals("9606")){// Homo Sapiens
								String old_symbol_lower = old_symbol.trim().toLowerCase();
								deprecatesymbol2entrez.put(old_symbol_lower, entrez);
							}
						}

						else
							firstLine = false;

					} catch (Exception e) {}
				}

				br.close();
				in.close();
				fstream.close();	
			} catch (Exception e) {}
		}
		return deprecatesymbol2entrez;
	}

	public static HashMap<String, String> getSymbol2Entrez() {
		if(symbol2entrez.isEmpty()){
			try {
				InputStream fstream = new FileInputStream(ncbi_table_path);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					try {
						if (!line.startsWith("#") && !line.equals("")) {
							String[] arr = line.split("\t");
							//if (arr[2].trim().toLowerCase().equals(type.trim().toLowerCase())) {

							String extendedInfo = arr[8];
							if (extendedInfo.contains("Name=")) {
								String[] extendedInfo_arr = extendedInfo.split(";");
								for (String data: extendedInfo_arr) {

									if (data.toLowerCase().trim().startsWith("name")) {
										String[] name_split = data.split("=");

										String symbol = name_split[name_split.length-1];

										//if(symbol.equals(symbol_from_gencode)){
										String entrez_tmp = getEntrezFromNCBIline(extendedInfo);
										symbol2entrez.put(symbol.trim().toLowerCase(),entrez_tmp.trim().toLowerCase());
										//}
									}
								}
							}
							//}
						}
					} catch (Exception e) {}
				}
				br.close();
				in.close();
				fstream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return symbol2entrez;
	}


	private static String getEntrezFromNCBIline(String arr) {
		String entrez = "";
		for (String data: arr.split(";")) {
			if (data.toLowerCase().trim().contains("geneid")) {
				for (String data1: data.split(",")) {

					if (data1.toLowerCase().trim().contains("geneid")) {
						String[] name_split = data1.split(":");
						entrez = name_split[name_split.length-1];
						break;
					}
				}
			}
		}
		return entrez;
	}

}
