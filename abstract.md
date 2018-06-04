Nowdays, several organizations are focusing on collecting heterogeneous data, 
first among all is the Genomic Data Commons (GDC), an organization directly 
funded by the US government that collects an huge amount of experimental and
clinical data about more than 30 different types of cancer from different 
research centers on all over the United States. These data are maintained
by GDC that supports two main research projects: The Cancer Genome Atlas (TCGA),
with experimental results about thousand of patients affected by different
kind of tumors, and Therapeutically Applicable Research to Generate Effective 
Treatments (TARGET) about cancer in children.

Unfortunately, due to the heterogeneous nature of the data, it is not easy
to analyse them under an integrated oriented approach.
Here we propose OpenGDC, an open-source Java software to automatically extract
and standardise public accessible GDC genomic and clinical data to allow 
researchers to easily perform ad-hoc integrated genomic analyses.

Our tool is able to convert these genomic data to the most common Browser 
Extensible Data format (BED) extending them with additional information 
retrieved from different data sources (i.e., HUGO, Gencode, and miRBase),
unifying all the original GDC experimental data (i.e., Somatic Mutation,
Methylation, Copy Number, Gene-, Isoform-, miRNA-  Expression Quantification).

OpenGDC also provides a data standardization procedure for clinical and
biospecimen data merging these information in a single tabular file
with additional data retrieved exploiting the public GDC APIs. 
It is also able to automatically manage data redundancy that is inevitably 
inferred during the merge of these meta information (e.g., the 'gender' 
of the pationt related to a particular tissue is often replicated in both 
clinical and biospecimen sources).

Our data are fully supported by frameworks like GeCo that exploits a SQL-like 
declarative language (GenoMetric Query Language - GMQL) to make integrative 
queries on different genomic data.

This is a valid example about how our data standardization approach makes 
the integrative analyses easy to be performed by ad-hoc frameworks.

We provide an open access FTP repository containing all the public accessible
genomic and clinical data from GDC already converted with OpenGDC resulting
in 1.5TB of data volume.
An automatic procedure to maintain the repository up to date with GDC has been
implemented. The repository is accessible through the following link:
ftp://bioinformatics.iasi.cnr.it/opengdc/bed/

Finally, OpenGDC is freely available at http://bioinf.iasi.cnr.it/opengdc/
