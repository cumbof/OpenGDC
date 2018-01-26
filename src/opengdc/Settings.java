/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc;

import java.io.File;

/**
 *
 * @author fabio
 */
public class Settings {
    
    // debug flag
    private static final boolean DEBUG = true;
    
    // build number
    private static final String BUILD_NUMBER = "0001";
    public static String getBuildNumber() {
        return BUILD_NUMBER;
    }
    
    // ****************** tmp dir ******************
    private static final String TMP_DIR = "./appdata/tmp/";
    public static String getTmpDir() {
        String tmpDir = TMP_DIR;
        if (DEBUG) tmpDir = "/Users/fabio/Downloads/test_gdc_download/tmp/";
        if (!(new File(tmpDir)).exists())
            (new File(tmpDir)).mkdir();
        return tmpDir;
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
    
    private static final String OPEN_GDC_PAGE_URL = "http://bioinformatics.iasi.cnr.it/opengdc/";
    public static String getOpenGDCPageURL() {
        return OPEN_GDC_PAGE_URL;
    }
    
    private static final String OPEN_GDC_FTP_REPO_BASE = "ftp://bioinformatics.iasi.cnr.it/opengdc/";
    public static String getOpenGDCFTPRepoBase() {
        return OPEN_GDC_FTP_REPO_BASE;
    }
    
    private static final String OPEN_GDC_FTP_REPO_ORIGINAL = "original";
    public static String getOpenGDCFTPRepoOriginal() {
        return getOpenGDCFTPRepoBase()+OPEN_GDC_FTP_REPO_ORIGINAL+"/";
    }
    
    private static final String OPEN_GDC_FTP_REPO_BED_CONVERTED = "bed";
    public static String getOpenGDCFTPRepoBEDConverted() {
        return getOpenGDCFTPRepoBase()+OPEN_GDC_FTP_REPO_BED_CONVERTED+"/";
    }
    
    private static final String OPEN_GDC_FTP_REPO_TCGA = "tcga";
    private static final String OPEN_GDC_FTP_REPO_TARGET = "target";
    public static String getOpenGDCFTPRepoProgram(String program, boolean original) {
        if (program.trim().toLowerCase().contains("tcga")) {
            if (original) return getOpenGDCFTPRepoOriginal()+OPEN_GDC_FTP_REPO_TCGA+"/";
            return getOpenGDCFTPRepoBEDConverted()+OPEN_GDC_FTP_REPO_TCGA+"/";
        }
        else if (program.trim().toLowerCase().contains("target")) {
            if (original) return getOpenGDCFTPRepoOriginal()+OPEN_GDC_FTP_REPO_TARGET+"/";
            return getOpenGDCFTPRepoBEDConverted()+OPEN_GDC_FTP_REPO_TARGET+"/";
        }
        return "";
    }
    
    private static final String OPEN_GDC_FTP_CONVERTED_DATA_FORMAT = "bed";
    public static String getOpenGDCFTPConvertedDataFormat() {
        return OPEN_GDC_FTP_CONVERTED_DATA_FORMAT;
    }
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    
    
    private static final String MIRBASE_HSA_GFF3_DATA = "./appdata/mirbase/hsa.gff3";
    public static String getMirbaseHsaDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/mirbase/hsa.gff3";
        return MIRBASE_HSA_GFF3_DATA;
    }
    
    /***************************************** old method: querying NCBI *****************************************/
    /*private static final String NCBI_DATA = "./appdata/ncbi/GRCh38_data.txt";
    public static String getNCBIDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/ncbi/GRCh38_data.txt";
        return NCBI_DATA;
    }*/
    /*************************************************************************************************************/
    private static final String NCBI_DATA = "./appdata/ncbi/ref_GRCh38.p2_top_level.gff3";
    public static String getNCBIDataPath() {
        if (DEBUG) return "/Users/eleonora/NetBeansProjects/OpenGDC/package/appdata/ncbi/ref_GRCh38.p2_top_level.gff3";
        return NCBI_DATA;
    }
    
    private static final String HISTORY_NCBI_DATA = "./appdata/ncbi/gene_history.txt";
    public static String getHistoryNCBIDataPath() {
        if (DEBUG) return "/Users/eleonora/NetBeansProjects/OpenGDC/package/appdata/ncbi/gene_history.txt";
        return HISTORY_NCBI_DATA;
    }
    
    private static final String GENENAMES_DATA = "./appdata/genenames/hgnc_complete_set.txt";
    public static String getGENENAMESDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/genenames/hgnc_complete_set.txt";
        return GENENAMES_DATA;
    }

    private static final String ILLUMINA_DATA = "./appdata/illumina/humanMethylation.txt";
    public static String getILLUMINADataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/illumina/humanMethylation.txt";
        return ILLUMINA_DATA;
    }
    
    private static final String ENSEMBL_DATA = "./appdata/ensembl/Homo_sapiens.GRCh38.77.gtf";
    public static String getENSEMBLDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/ensembl/Homo_sapiens.GRCh38.77.gtf";
        return ENSEMBL_DATA;
    }

    private static final String GENCODE_DATA = "./appdata/gencode/gencode.v22.annotation.gtf";
    public static String getGENCODEDataPath() {
        if (DEBUG) return "/Users/fabio/NetBeansProjects/OpenGDC/package/appdata/gencode/gencode.v22.annotation.gtf";
        return GENCODE_DATA;
    }
    
}
