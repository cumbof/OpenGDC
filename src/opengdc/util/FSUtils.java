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
        str = str.trim();
        if (str.length() > 0) {
            StringBuilder sb = new StringBuilder();
            int countSpecialChars = 0;
            for (int i = 0; i < str.length(); i++) {
                if (i == 0 && String.valueOf(str.charAt(i)).matches("[a-zA-Z]")) {
                    sb.append(str.charAt(i));
                    countSpecialChars = 0;
                } 
                else if (i > 0 && String.valueOf(str.charAt(i)).matches("[a-zA-Z0-9]")) {
                    sb.append(str.charAt(i));
                    countSpecialChars = 0;
                }
                else {
                    if (countSpecialChars == 0) {
                        if (i < str.length()-1) {
                            if (i == 0) {
                                try {
                                    sb.append("_");
                                    String integerChar = String.valueOf(Integer.valueOf(str.charAt(i)));
                                    sb.append(integerChar);
                                }
                                catch (Exception e) { }
                            }
                            else
                                sb.append("_");
                        }
                    }
                    countSpecialChars++;
                }
            }
            return sb.toString();
        }
        return null;
    }

}
