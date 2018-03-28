-- DB Schema

-- 'opengdc_data' table
create table opengdc_data (
    opengdc_id                  text,
    tumor_abbreviation          text,
    experiment_type             text,
    data_name                   text,
    data_type                   text,
    md5_checksum                text,
    last_update_timestamp       text,
    data_url                    text,
    source                      text,
    primary key (opengdc_id, data_type)
);

-- 'opengdc_biospecimen' table
create table opengdc_biospecimen (
    opengdc_id                  text primary key,
    meta_attribute              text,
    meta_value                  text
);

-- 'opengdc_clinical' table
create table opengdc_clinical (
    opengdc_id                  text primary key,
    meta_attribute              text,
    meta_value                  text
);

-- 'opengdc_manually_curated' table
create table opengdc_manually_curated (
    opengdc_id                  text primary key,
    meta_attribute              text,
    meta_value                  text
);

-- 'opengdc_experiment_header' table
create table opengdc_experiment_header (
    experiment_type             text,
    header_name                 text,
    header_position             int,
    primary key (experiment_type, header_name)
);

-- 'opengdc_annotation_geneexpression' table
create table opengdc_annotation_geneexpression (
    chrom                       text,
    start_position              bigint,
    end_position                bigint,
    strand                      text,
    ensembl_gene_id             text primary key,
    entrez_gene_id              text,
    gene_symbol                 text
);

-- 'opengdc_annotation_humanmethylation' table
create table opengdc_annotation_humanmethylation (
    chrom                       text,
    start_position              bigint,
    end_position                bigint,
    strand                      text,
    composite_element_ref       text,
    gene_symbol                 text,
    entrez_gene_id              text,
    gene_type                   text,
    ensembl_transcript_id       text,
    position_to_tss             text,
    all_gene_symbols            text,
    all_entrez_gene_ids         text,
    all_gene_types              text,
    all_ensembl_transcript_ids  text,
    all_positions_to_tss        text,
    cgi_coordinate              text,
    feature_type                text,
    platform                    text,
    primary key (composite_element_ref, platform)
);

-- 'opengdc_annotation_experiments' table
create table opengdc_annotation_experiments (
    experiment_name             text primary key
);
