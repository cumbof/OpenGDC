-- DB Schema

-- 'opengdc_data' table
create table opengdc_data (
    opengdc_id              text,
    tumor_abbreviation      text,
    experiment_type         text,
    data_name               text,
    data_type               text,
    md5_checksum            text,
    last_update_timestamp   text,
    data_url                text,
    source                  text,
    primary key (opengdc_id, data_type)
);

-- 'opengdc_clinical' table
create table opengdc_clinical (
    opengdc_id              primary key text,
    meta_attribute          text,
    meta_value              text
)

-- 'opengdc_biospecimen' table
create table opengdc_biospecimen (
    opengdc_id              primary key text,
    meta_attribute          text,
    meta_value              text
)

-- 'opengdc_manually_curated' table
create table opengdc_manually_curated (
    opengdc_id              primary key text,
    meta_attribute          text,
    meta_value              text
)
