/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc;

/**
 *
 * @author fabio
 */
public class Settings {
    
    // debug flag
    private static final boolean DEBUG = true;
    
    
    // ****************** tmp dir ******************
    private static final String TMP_DIR = "./appdata/tmp/";
    public static String getTmpDir() {
        if (DEBUG) return "/Users/fabio/Downloads/test_gdc_download/tmp/";
        return TMP_DIR;
    }
    // *********************************************
    
    
    // ############### download tab ################
    private static String outputGDCfolder = "";
    public static String getOutputGDCFolder() {
        return outputGDCfolder;
    }
    public static void setOutputGDCFolder(String path) {
        outputGDCfolder = path;
    }
    // #############################################
    
    
    // --------------- convert tab -----------------
    private static String outputConvertedfolder = "";
    public static String getOutputConvertedFolder() {
        return outputConvertedfolder;
    }
    public static void setOutputConvertedFolder(String path) {
        outputConvertedfolder = path;
    }
    
    private static String inputGDCfolder = "";
    public static String getInputGDCFolder() {
        return inputGDCfolder;
    }
    public static void setInputGDCFolder(String path) {
        inputGDCfolder = path;
    }
    // ---------------------------------------------
    
    
    // @@@@@@@@@@@@@@@@@@@@ url @@@@@@@@@@@@@@@@@@@@
    private static final String GDC_DATA_PORTAL_URL = "https://gdc.cancer.gov/";
    public static String getGDCDataPortalURL() {
        return GDC_DATA_PORTAL_URL;
    }
    
    private static final String OPEN_GDC_PAGE_URL = "http://bioinf.iasi.cnr.it/opengdc/";
    public static String getOpenGDCPageURL() {
        return OPEN_GDC_PAGE_URL;
    }
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    
    
    private static final String MIRBASE_HSA_GFF3_DATA = "./appdata/mirbase/hsa.gff3";
    public static String getMirbaseHsaDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/mirbase/hsa.gff3";
        return MIRBASE_HSA_GFF3_DATA;
    }
    
    private static final String NCBI_DATA = "./appdata/ncbi/data.txt";
    public static String getNCBIDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/ncbi/data.txt";
        return NCBI_DATA;
    }
    
    private static final String GENENAMES_DATA = "./appdata/genenames/hgnc_complete_set.txt";
    public static String getGENENAMESDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/genenames/hgnc_complete_set.txt";
        return GENENAMES_DATA;
    }
    
    private static final String ENSEMBL_DATA = "./appdata/ensembl/Homo_sapiens.GRCh38.88.chr.gff3";
    public static String getENSEMBLDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/ensembl/Homo_sapiens.GRCh38.88.chr.gff3";
        return ENSEMBL_DATA;
    }
    
}
