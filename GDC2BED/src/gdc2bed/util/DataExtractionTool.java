/*
 * Application: GDC2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package gdc2bed.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class DataExtractionTool {
    
    private static HashSet<String> experiments_path = new HashSet<>();
    private static HashSet<String> uncompressed_folders_path = new HashSet<>();
    
    public static HashSet<String> getExperimentsPathList() {
        return experiments_path;
    }
    
    public static HashSet<String> getUncompressedFoldersPathList() {
        return uncompressed_folders_path;
    }

    public static boolean uncompressTarGz(File tarFile, File destDir, boolean removeSource) {
        experiments_path = new HashSet<>();
        uncompressed_folders_path = new HashSet<>();
        uncompressed_folders_path.add(destDir.getAbsolutePath());
        
        try {
            destDir.mkdirs();
            TarArchiveInputStream tarIn = null;

            if (tarFile.getName().toLowerCase().endsWith(".gz"))
                tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarFile))));
            else
                tarIn = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
            
            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
            // tarIn is a TarArchiveInputStream
            while (tarEntry != null) {// create a file with the same name as the tarEntry
                File destPath = new File(destDir, tarEntry.getName());
                System.out.println("working: " + destPath.getCanonicalPath());
                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();
                } else {
                    destPath.getParentFile().mkdirs();

                    //System.err.println(destPath.getAbsolutePath());
                    destPath.createNewFile();
                    //byte [] btoRead = new byte[(int)tarEntry.getSize()];
                    byte[] btoRead = new byte[1024];
		            //FileInputStream fin 
                    //  = new FileInputStream(destPath.getCanonicalPath());
                    BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
                    int len = 0;

                    while ((len = tarIn.read(btoRead)) != -1) {
                        bout.write(btoRead, 0, len);
                    }

                    bout.close();
                    btoRead = null;

                    if (destPath.getName().toLowerCase().trim().endsWith(".gz")) {
                        String destAbsolutePath = destPath.getAbsolutePath();
                        String uncompressedAbsolutePath = destAbsolutePath.substring(0, destAbsolutePath.length()-3);
                        File uncompressedFile = new File(uncompressedAbsolutePath);
                        boolean uncompressed = uncompressGz(destPath, uncompressedFile, removeSource);
                        if (uncompressed)
                            experiments_path.add(uncompressedFile.getAbsolutePath());
                    }
                }
                tarEntry = tarIn.getNextTarEntry();
            }
            tarIn.close();

            boolean uncompressed = false;
            if (destDir.exists() && destDir.isDirectory()) {
                uncompressed = true;
                if (removeSource)
                    tarFile.delete();
            }
            
            return uncompressed;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }
    
    public static boolean uncompressGz(File gzFile, File destFile, boolean removeSource) {
        try {
            FileInputStream fis = new FileInputStream(gzFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            
            //GzipCompressorInputStream gis = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(gzFile)));
            
            //ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.toByteArray(fis));
            //GZIPInputStream gis = new GZIPInputStream(bais);
            
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1)
                fos.write(buffer, 0, len);
            fos.close();
            gis.close();
            //bais.close();
            fis.close();
            
            boolean uncompressed = false;
            if (destFile.exists()) {
                uncompressed = true;
                if (removeSource)
                    gzFile.delete();
            }
            
            return uncompressed;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }

}
