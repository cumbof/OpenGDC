import os, datetime, pytz
from db import *

root = '/FTP/ftp-root/opengdc/bed/';
base_url = 'http://bioinformatics.iasi.cnr.it/opengdc/bed/';
md5_checksum_filename = 'md5checksum.txt';
exclude_dirs = ['additional_output_files'];
exclude_files = ['header.schema', 'md5checksum.txt', 'meta_dictionary.txt', 'exp_info.tsv'];
annotations_dir_name = '_annotations';

experiment_data_types = ['.bed'];
metadata_data_types = ['.meta'];
static_source = 'NCI Genomic Data Commons';
current_timestamp = datetime.datetime.now(tz=pytz.utc).isoformat();

def readGeneExpressionAnnotations(annotations_dir_path):
    query_db('insert into opengdc_annotation_experiments (experiment_name) values (\'GeneExpression\');');
    for file in os.listdir(annotations_dir_path):
        if file.endswith('.bed'):
            with open(os.path.join(annotations_dir_path, file)) as f:
                for line in f:
                    #line = line.strip();
                    if line.strip() is not "":
                        line_split = line.split("\t");
                        chrom = line_split[0];
                        start_position = line_split[1];
                        end_position = line_split[2];
                        strand = line_split[3];
                        ensembl_gene_id = line_split[4];
                        entrez_gene_id = line_split[5];
                        gene_symbol = line_split[6];
                        query_db('insert into opengdc_annotation_geneexpression (chrom, start_position, end_position, strand, ensembl_gene_id, entrez_gene_id, gene_symbol) values (\''+chrom+'\', '+start_position+', '+end_position+', \''+strand+'\', \''+ensembl_gene_id+'\', \''+entrez_gene_id+'\', \''+gene_symbol+'\');');
                        
def readHumanMethylationAnnotations(annotations_dir_path):
    platform = '';
    if annotations_dir_path.endswith("27"):
        platform = 'HumanMethylation27';
        query_db('insert into opengdc_annotation_experiments (experiment_name) values (\'HumanMethylation27\');');
    elif annotations_dir_path.endswith("450"):
        platform = 'HumanMethylation450';
        query_db('insert into opengdc_annotation_experiments (experiment_name) values (\'HumanMethylation450\');');
    for file in os.listdir(annotations_dir_path):
        if file.endswith('.bed'):
            with open(os.path.join(annotations_dir_path, file)) as f:
                for line in f:
                    #line = line.strip();
                    if line.strip() is not "":
                        line_split = line.split("\t");
                        chrom = line_split[0];
                        start_position = line_split[1];
                        end_position = line_split[2];
                        strand = line_split[3];
                        composite_element_ref = line_split[4];
                        gene_symbol = line_split[5];
                        entrez_gene_id = line_split[6];
                        gene_type = line_split[7];
                        ensembl_transcript_id = line_split[8];
                        position_to_tss = line_split[9];
                        all_gene_symbols = line_split[10];
                        all_entrez_gene_ids = line_split[11];
                        all_gene_types = line_split[12];
                        all_ensembl_transcript_ids = line_split[13];
                        all_positions_to_tss = line_split[14];
                        cgi_coordinate = line_split[15];
                        feature_type = line_split[16];
                        query_db('insert into opengdc_annotation_humanmethylation (chrom, start_position, end_position, strand, composite_element_ref, gene_symbol, entrez_gene_id, gene_type, ensembl_transcript_id, position_to_tss, all_gene_symbols, all_entrez_gene_ids, all_gene_types, all_ensembl_transcript_ids, all_positions_to_tss, cgi_coordinate, feature_type, platform) values (\''+chrom+'\', '+start_position+', '+end_position+', \''+strand+'\', \''+composite_element_ref+'\', \''+gene_symbol+'\', \''+entrez_gene_id+'\', \''+gene_type+'\', \''+ensembl_transcript_id+'\', \''+position_to_tss+'\', \''+all_gene_symbols+'\', \''+all_entrez_gene_ids+'\', \''+all_gene_types+'\', \''+all_ensembl_transcript_ids+'\', \''+all_positions_to_tss+'\', \''+cgi_coordinate+'\', \''+feature_type+'\', \''+platform+'\');');

def storeAnnotations(annotations_dir_path):
    for folder in os.listdir(annotations_dir_path):
        if os.path.isdir(os.path.join(annotations_dir_path, folder)):
            if folder.startswith('GeneExpression'):
                print 'Processing annotation ' + folder;
                readGeneExpressionAnnotations(os.path.join(annotations_dir_path, folder));
            elif folder.startswith('HumanMethylation'):
                print 'Processing annotation ' + folder;
                readHumanMethylationAnnotations(os.path.join(annotations_dir_path, folder));

def storeMeta(dir_path, file_name, opengdc_id):
    with open(os.path.join(dir_path, file_name)) as file:
        for line in file:
            if line.strip() is not "":
                line_split = line.strip().split("\t");
                table = "";
                meta_attribute = line_split[0];
                if meta_attribute.startswith('biospecimen'):
                    table = 'opengdc_biospecimen';
                elif meta_attribute.startswith('clinical'):
                    table = 'opengdc_clinical';
                elif meta_attribute.startswith('manually_curated'):
                    table = 'opengdc_manually_curated';
                if table.strip() is not "":
                    meta_value = line_split[1];
                    query_db('insert into '+table+' (opengdc_id, meta_attribute, meta_value) values (\''+opengdc_id+'\', \''+meta_attribute+'\', \''+meta_value+'\');');

def getChecksum(dir_path, file_name):
    md5_checksum_filepath = os.path.join(dir_path, md5_checksum_filename);
    checksum = "";
    with open(md5_checksum_filepath) as file:
       for line in file:
           if line.strip() is not "":
               line_split = line.strip().split("\t");
               if line_split[0] == file_name:
                   checksum = line_split[1];
                   break;
    return checksum;

def processDataType(program, tumor, datatype, bedfiles_root):
    for file in os.listdir(bedfiles_root):
        filename, file_extension = os.path.splitext(file);
        # take frace of the file (experiment or metadata)
        if file_extension in experiment_data_types or file_extension in metadata_data_types:
            opengdc_id = filename;
            tumor_abbreviation = tumor;
            experiment_type = datatype;
            data_name = filename;
            data_type = file_extension;
            md5_checksum = getChecksum(bedfiles_root, file);
            last_update_timestamp = current_timestamp;
            data_url = base_url + program + '/' + tumor + '/' + datatype + '/' + file;
            source = static_source;
            query_db('insert into opengdc_data (opengdc_id, tumor_abbreviation, experiment_type, data_name, data_type, md5_checksum, last_update_timestamp, data_url, source) values (\''+opengdc_id+'\', \''+tumor_abbreviation+'\', \''+experiment_type+'\', \''+data_name+'\', \''+data_type+'\', \''+md5_checksum+'\', \''+last_update_timestamp+'\', \''+data_url+'\', \''+source+'\');');
            # if the current file is metadata -> take trace of its content
            if file_extension in metadata_data_types:
                storeMeta(bedfiles_root, file, opengdc_id);

def walkDirs(programs_root):
    # just for bed data
    annotations_root = os.path.join(programs_root, annotations_dir_name);
    storeAnnotations(annotations_root);
    for program in os.listdir(programs_root):
        if os.path.isdir(os.path.join(programs_root, program)):
            tumors_root = os.path.join(programs_root, program);        
            for tumor in os.listdir(tumors_root):
                if os.path.isdir(os.path.join(tumors_root, tumor)) and tumor not in exclude_dirs:
                    datatypes_root = os.path.join(tumors_root, tumor);
                    for datatype in os.listdir(datatypes_root):
                        if os.path.isdir(os.path.join(datatypes_root, datatype)):
                            bedfiles_root = os.path.join(datatypes_root, datatype);
                            print 'Processing data ' + bedfiles_root;
                            processDataType(program, tumor, datatype, bedfiles_root);

if __name__ == '__main__':
    initializeDB();
    walkDirs(root);
