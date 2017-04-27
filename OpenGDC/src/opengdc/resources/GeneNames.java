package opengdc.resources;

import java.io.File;
import java.io.IOException;

import opengdc.util.QueryParser;
import opengdc.util.XMLReader;

public class GeneNames {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		System.out.println(MethylationBetaValueParser.getStrand("ABAT"));
//	}

	
	public static String retriveEntrez_idFromGene_symbol(String geneSymbol) throws IOException{
		String hugoFetchQuery = "http://rest.genenames.org/fetch/symbol/" + geneSymbol;
       File hugoXml_tmp = File.createTempFile("hugo_tmp", "tmp");
        QueryParser.downloadDataFromUrl(hugoFetchQuery, hugoXml_tmp.getAbsolutePath(), 0);
        System.out.println(hugoFetchQuery);
        String entrez = XMLReader.getEntrezFromHugo(hugoXml_tmp.getAbsolutePath());
		return entrez;
	}
}
