/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author fabio
 */
public class DownloadDataTest {
    
    public static final String UUID = "e311d293-e307-4a96-a363-aee16a60fa6c";
    public static final String URL = "https://gdc-api.nci.nih.gov/data/"+UUID;
    public static final String FILE_OUTPUT_PATH = "/Users/fabio/Downloads/test_gdc_download/test/e311d293-e307-4a96-a363-aee16a60fa6c.txt";
     
    /*
        http://www.matjazcerkvenik.si/developer/java-download-file-via-http.php
    
        ReadableByteChannel, starting from first byte (0) until the last byte in cache, 
        but no more than maximum number of bytes (Long.MAX_VALUE).
        So the problem is: if cache does not contain complete file, also Java cannot 
        download complete file. For larger files this can occur quite often.
    
        Try a different approach.
    */
    
    public static void main(String[] args) throws Exception {
        URL url = new URL(URL);
        URLConnection connection = url.openConnection();
        FileOutputStream fos = new FileOutputStream(FILE_OUTPUT_PATH);
        PrintStream out = new PrintStream(fos);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            String nextLine = reader.readLine();
            if (nextLine != null)
                out.println(line);
            else
                out.print(line);
            line = nextLine;
        }
        reader.close();
        out.close();
        fos.close();
    }
    
}
