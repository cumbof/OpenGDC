package opengdc.test;

import java.util.HashMap;
import opengdc.util.MetadataHandler;

/**
 *
 * @author fabio
 */
public class RetrieveMetadataFromXLSX {
    
    public static void main(String[] args) {
        String xlsx_file_path = "/Users/fabio/Downloads/test_gdc_download/3e116cc7-acad-4c25-80cd-8ee62e09825a_TARGET_AML_SampleMatrix_Discovery_20160921.xlsx";
        HashMap<String, HashMap<String, String>> metadata = MetadataHandler.getXLSXMap(null, xlsx_file_path, "case usi");
        System.err.println(metadata.size());
        for (String index: metadata.keySet()) {
            System.err.println(index);
            for (String meta: metadata.get(index).keySet())
                System.err.println("\t"+meta+"\t"+metadata.get(index).get(meta));
        }
    }
    
}
