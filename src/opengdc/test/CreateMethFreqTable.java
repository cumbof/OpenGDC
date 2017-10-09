/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import opengdc.util.GDCData;

/**
 *
 * @author fabio
 */
public class CreateMethFreqTable {
    
    private static final String ROOT = "/galaxy/home/fabio/meth/";
    
    public static void main(String[] args) {
        ArrayList<String> diseases = new ArrayList<>(GDCData.getBigGDCDataMap().get("TCGA").keySet());
        for (String disease: diseases) {
            System.err.println("processing "+disease);
            disease = disease.toLowerCase().split("-")[1];
            File data_folder = new File(ROOT+disease+"/original/");
            if (data_folder.exists()) {
                try {
                    System.err.println("retrieving site2gene map");
                    //HashMap<String, String> site2gene = getSite2GeneMap(data_folder);
                    //ArrayList<String> sites = new ArrayList<>(site2gene.keySet());
                    ArrayList<String> sites = getSites(data_folder);
                    System.err.println("retrieving aliquots");
                    ArrayList<String> aliquots = getAliquots(data_folder);
                    System.err.println("retrieving beta values");
                    double[][] beta_values = getBetaValues(data_folder, sites, aliquots);
                    if (beta_values != null) {
                        String matrixFilePath = ROOT+disease+"_beta_matrix.tsv";
                        System.err.println("printing matrix");
                        //printBetaValues(matrixFilePath, site2gene, sites, aliquots, beta_values);
                        printBetaValues(matrixFilePath, null, sites, aliquots, beta_values);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.err.println("--------------");
        }
    }
    
    private static ArrayList<String> getSites(File data_folder) {
    //private static HashMap<String, String> getSite2GeneMap(File data_folder) {
        //HashMap<String, String> site2gene = new HashMap<>();
        ArrayList<String> sites = new ArrayList<>();
        for (File f: data_folder.listFiles()) {
            if (f.getName().toLowerCase().endsWith("txt")) {
                try {
                    InputStream fstream = new FileInputStream(f.getAbsolutePath());
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().equals("")) {
                            String[] line_split = line.split("\t");
                            String site = line_split[4];
                            //String gene_symbol = line_split[6];
                            //site2gene.put(site, gene_symbol);
                            sites.add(site);
                        }
                    }
                    br.close();
                    in.close();
                    fstream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        //return site2gene;
        return sites;
    }

    private static ArrayList<String> getAliquots(File data_folder) {
        ArrayList<String> aliquots = new ArrayList<>();
        for (File f: data_folder.listFiles()) {
            if (f.getName().toLowerCase().endsWith("bed")) {
                String[] f_name_split = f.getName().split("\\.");
                String aliquot = f_name_split[0];
                aliquots.add(aliquot);
            }
        }
        return aliquots;
    }

    private static double[][] getBetaValues(File data_folder, ArrayList<String> sites, ArrayList<String> aliquots) throws Exception {
        // +2 for key: aliquot+tissue_type
        // +2 for value: site+gene
        double[][] beta_values = new double[sites.size()][aliquots.size()];
        for (int i=0; i<aliquots.size(); i++) {
            String aliquot = aliquots.get(i);
            String bed_file_path = data_folder.getAbsolutePath()+"/"+aliquot+".bed";
            if ((new File(bed_file_path)).exists()) {
                try {
                    InputStream fstream = new FileInputStream(bed_file_path);
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().equals("")) {
                            String[] line_split = line.split("\t");
                            String site = line_split[4];
                            String beta_value_str = line_split[5];
                            double beta_value = Double.NaN;
                            if (!beta_value_str.toLowerCase().trim().equals("null"))
                                beta_value = Double.valueOf(beta_value_str);
                            int row = sites.indexOf(site);
                            int column = i;
                            beta_values[row][column] = beta_value;
                        }
                    }
                    br.close();
                    in.close();
                    fstream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else throw new Exception("");
        }
        return beta_values;
    }

    private static void printBetaValues(String matrixFilePath, HashMap<String, String> site2gene, ArrayList<String> sites, ArrayList<String> aliquots, double[][] beta_values) {
        try {
            FileOutputStream fos = new FileOutputStream(matrixFilePath);
            PrintStream out = new PrintStream(fos);
            //print header
            //String line = "\t\t";
            String line = "\t";
            for (int i=0; i<aliquots.size(); i++)
                line = line + aliquots.get(i) + "\t";
            line = line.substring(0, line.length()-1);
            out.println(line);
            
            //print data
            for (int i=0; i<sites.size(); i++) {
                String site = sites.get(i);
                //System.err.println("> "+site);
                /*String gene = "";
                if (site2gene.containsKey(site))
                    gene = site2gene.get(site);*/
                //line = site + "\t" + gene + "\t";
                line = site + "\t";
                for (int j=0; j<beta_values[i].length; j++)
                    line = line + String.valueOf(beta_values[i][j]) + "\t";
                line = line.substring(0, line.length()-1);
                out.println(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
