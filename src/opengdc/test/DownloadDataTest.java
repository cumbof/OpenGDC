/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author fabio
 */
public class DownloadDataTest {
    
    public static final String UUID = "e311d293-e307-4a96-a363-aee16a60fa6c";
    public static final String URL = "https://gdc-api.nci.nih.gov/data/"+UUID;
    public static final String FILE_OUTPUT_PATH = "/Users/fabio/Downloads/test_gdc_download/test/e311d293-e307-4a96-a363-aee16a60fa6c.txt";
    
    public static void main(String[] args) throws Exception {
        URL url = new URL(URL);
        //ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(FILE_OUTPUT_PATH);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpConn.getInputStream();
        
        // METHOD 1
        //fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        
        // METHOD 2
        /*long read = 0;
        long pos = 0;
        while ((read = fos.getChannel().transferFrom(rbc, pos, Long.MAX_VALUE)) > 0) {
            System.err.println("iteration");
            pos += read;
        }*/
        
        // METHOD 3
        int bytesRead = -1;
        byte[] buffer = new byte[4096];
        while ((bytesRead = inputStream.read(buffer)) != -1)
            fos.write(buffer, 0, bytesRead);
        
        inputStream.close();
        httpConn.disconnect();
        fos.close();
        
        //rbc.close();
    }
    
}
