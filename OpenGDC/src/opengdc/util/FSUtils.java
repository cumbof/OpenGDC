/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.util;

import java.io.File;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class FSUtils {
    
    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
    
    public static String getFileExtension(File file) {
        String fileName = file.getName().toLowerCase().trim();
        String[] fileNameSplit = fileName.split("\\.");
        return "."+fileNameSplit[fileNameSplit.length-1];
    }

    public static int acceptedFilesInFolder(String inPath, HashSet<String> acceptedInputFileFormats) {
        File[] files = (new File(inPath)).listFiles();
        int acceptedFileCount = 0;
        for (File f: files) {
            if (f.isFile()) {
                if (acceptedInputFileFormats.contains(getFileExtension(f)))
                    acceptedFileCount++;
            }
        }
        return acceptedFileCount;
    }
    
    /* https://stackoverflow.com/a/7440915 */
    public static String stringToValidJavaIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isJavaIdentifierStart(str.charAt(0)) || i > 0 && Character.isJavaIdentifierPart(str.charAt(i)))
                sb.append(str.charAt(i));
            else
                sb.append("_");
        }
        return sb.toString();
    }
    
}
