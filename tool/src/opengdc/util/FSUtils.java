package opengdc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
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
    
    public static String getFileChecksum(MessageDigest digest, File file) throws IOException{
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< bytes.length ;i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
    
    public static boolean filePrefixExists(String suffixName, String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            for (String fileName: folder.list()) {
                if (fileName.endsWith(suffixName))
                    return true;
            }
        }
        return false;
    }

    public static  void deleteFileWithPrefix(String filePrefix, String dir_path) {
        File targetFile = null;
        for (File file: (new File(dir_path)).listFiles()) {
            if (file.getName().startsWith(filePrefix) && file.getName().toLowerCase().endsWith(".bed")) {
                targetFile = file;
                break;
            }
        }
        if (targetFile != null)
            targetFile.delete();
    }
    
    public static void main(String[] args) {
        String test = "clinical__cases__aliquot_id";
        System.err.println("Original String: " + test);
        System.err.println("Valid Java Identifier: " + stringToValidJavaIdentifier(test));
    }

}
