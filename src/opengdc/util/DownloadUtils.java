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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import opengdc.GUI;

/**
 *
 * @author fabio
 */
public class DownloadUtils {
    
    private static final int DOWNLOAD_RECURSIVE_REQUEST_LIMIT = 100;
    
    public static boolean downloadDataFromUrl(String url, String out_path, int count) {
        try {
            //System.err.println("URL: "+url);
            URL tcga_url = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(tcga_url.openStream());
            FileOutputStream fos = new FileOutputStream(out_path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            GUI.appendLog(e.getMessage() + "\n\n");
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            GUI.appendLog(e.getMessage() + "\n\n");
            return false;
        } catch (UnknownHostException e) {
            //System.out.println(url + "\t" + count);
            count++;
            if (count > DOWNLOAD_RECURSIVE_REQUEST_LIMIT)
                return false;
            return downloadDataFromUrl(url, out_path, count); // if malformed url : loop!!!
        } catch (IOException e) {
            //Main.printException(e, true);
            //return false;
            //System.err.println(url + "\t" + count);
            count++;
            if (count > DOWNLOAD_RECURSIVE_REQUEST_LIMIT)
                return false;
            return downloadDataFromUrl(url, out_path, count);
        }
    }
    
}
