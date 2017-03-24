/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed;

/**
 *
 * @author fabio
 */
public class Settings {
    
    // debug flag
    private static final boolean DEBUG = true;
    
    
    // ****************** tmp dir ******************
    private static final String TMP_DIR = "./tmp/";
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
    
    private static final String GDC2BED_PAGE_URL = "http://bioinf.iasi.cnr.it/gdc2bed/";
    public static String getGDC2BEDPageURL() {
        return GDC2BED_PAGE_URL;
    }
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
}
