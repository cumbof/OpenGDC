package opengdc.integration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import opengdc.Settings;

public class IlluminaHumanMethylation {

	private static String illumia_human_methylation = Settings.getILLUMINADataPath();
	private static HashMap<String, String> cpgsite2strand = new HashMap<>();

	public static String getStrandFromCompositeElem(String composite_element_ref) {
		HashMap<String, String> data = getCpGsite2Strand();
		if (!data.isEmpty()) {
			String composite_element_ref_lower = composite_element_ref.trim().toLowerCase();
			if(data.containsKey(composite_element_ref_lower)) 
				return strandTranform(data.get(composite_element_ref_lower));
			else System.out.println(composite_element_ref_lower);
		}
		return null;
	}

	private static String strandTranform(String strand_old) {
		String strand = "";
		if(strand_old.trim().toLowerCase().equals("F".trim().toLowerCase()))
			strand = "+";
		if(strand_old.trim().toLowerCase().equals("R".trim().toLowerCase()))
			strand = "-";
		return strand;
	}

	public static HashMap<String, String> getCpGsite2Strand() {
		if (cpgsite2strand.isEmpty()) {
			try {
				InputStream fstream = new FileInputStream(illumia_human_methylation);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					try {
						line = line.concat(",fine");
						String[] arr = line.split(",");
						String cpgsite = arr[0];
						String strand = arr[4];
						cpgsite2strand.put(cpgsite.trim().toLowerCase(), strand);         

					} catch (Exception e) {}
				}
				br.close();
				in.close();
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cpgsite2strand;
	}

}
