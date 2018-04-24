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
    
    public static void main(String[] args) {
        String test = "clinical__cases__aliquot_id";
        System.err.println("Original String: " + test);
        System.err.println("Valid Java Identifier: " + stringToValidJavaIdentifier(test));
    }

}
