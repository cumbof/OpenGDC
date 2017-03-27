/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class GDCData {
    
    private static HashMap<String, HashMap<String, HashSet<String>>> program2tumor2datatype = new HashMap<>();
    
    private static void initBigGDCDataMap() {
        program2tumor2datatype = new HashMap<>();
        program2tumor2datatype.put("TARGET", new HashMap<>());
        program2tumor2datatype.put("TCGA", new HashMap<>());
        
        /** TARGET **/
        
        HashSet<String> dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        HashMap<String, HashSet<String>> tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-NBL", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-AML", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-WT", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);

        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-OS", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);

        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-RT", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);

        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TARGET");
        tumor2dataType.put("TARGET-CCSK", dataTypes);
        program2tumor2datatype.put("TARGET", tumor2dataType);

        /** TCGA **/
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-BRCA", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-GBM", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-OV", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-LUAD", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-UCEC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-KIRC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-HNSC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-LGG", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-THCA", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-LUSC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-PRAD", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-SKCM", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-COAD", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-STAD", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-BLCA", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-LIHC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-CESC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-KIRP", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-SARC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-LAML", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-ESCA", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-PAAD", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-PCPG", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-READ", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-TGCT", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-THYM", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-KICH", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-ACC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-MESO", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-UVM", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-DLBC", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);

        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-UCS", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);
        
        dataTypes = new HashSet<>();
        dataTypes.add("Gene Expression Quantification");
        dataTypes.add("Copy Number Segment");
        dataTypes.add("Masked Copy Number Segment");
        dataTypes.add("Methylation Beta Value");
        dataTypes.add("Isoform Expression Quantification");
        dataTypes.add("miRNA Expression Quantification");
        dataTypes.add("Biospecimen Supplement");
        dataTypes.add("Clinical Supplement");        
        dataTypes.add("Masked Somatic Mutation");
        tumor2dataType = program2tumor2datatype.get("TCGA");
        tumor2dataType.put("TCGA-CHOL", dataTypes);
        program2tumor2datatype.put("TCGA", tumor2dataType);        
    }
    
    public static HashMap<String, HashMap<String, HashSet<String>>> getBigGDCDataMap() {
        if (program2tumor2datatype.isEmpty()) initBigGDCDataMap();
        return program2tumor2datatype;
    }

}
