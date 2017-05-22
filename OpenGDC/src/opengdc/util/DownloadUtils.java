/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
