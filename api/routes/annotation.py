from flask import Response, redirect
from . import blueprint

import json

@blueprint.route("/annotation/_experiments")
def annotation_experiments():
    data = {
        'hits': [ ]
    };
    for opengdc_obj in query_db('select * from opengdc_annotation_experiments'):
        data['hits'].append(opengdc_obj[0]);
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/annotation/<experiment_type>/_all")
def annotation_experiments_all(experiment_type):
    data = { 
        'hits': [ ]
    };
    status = 200;
    if experiment_type is 'GeneExpression':
        for opengdc_obj in query_db('select * from opengdc_annotation_geneexpression'):
            res = { };
            res['chrom'] = opengdc_obj[0];
            res['start_position'] = opengdc_obj[1];
            res['end_position'] = opengdc_obj[2];
            res['strand'] = opengdc_obj[3];
            res['ensembl_gene_id'] = opengdc_obj[4];
            res['entrez_gene_id'] = opengdc_obj[5];
            res['gene_symbol'] = opengdc_obj[6];
            data['hits'].append(res);
    elif experiment_type.startswith('HumanMethylation'):
        for opengdc_obj in query_db('select * from opengdc_annotation_humanmethylation where platform=\''+experiment_type+'\''):
            res = { };
            res['chrom'] = opengdc_obj[0];
            res['start_position'] = opengdc_obj[1];
            res['end_position'] = opengdc_obj[2];
            res['strand'] = opengdc_obj[3];
            res['composite_element_ref'] = opengdc_obj[4];
            res['gene_symbol'] = opengdc_obj[5];
            res['entrez_gene_id'] = opengdc_obj[6];
            res['gene_type'] = opengdc_obj[7];
            res['ensembl_transcript_id'] = opengdc_obj[8];
            res['position_to_tss'] = opengdc_obj[9];
            res['all_gene_symbols'] = opengdc_obj[10];
            res['all_entrez_gene_ids'] = opengdc_obj[11];
            res['all_gene_types'] = opengdc_obj[12];
            res['all_ensembl_transcript_ids'] = opengdc_obj[13];
            res['all_positions_to_tss'] = opengdc_obj[14];
            res['cgi_coordinate'] = opengdc_obj[15];
            res['feature_type'] = opengdc_obj[16];
            res['platform'] = opengdc_obj[17];
            data['hits'].append(res);
    if not data['hits']:
        data['message'] = "The specified opengdc_id does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/annotation/<experiment_type>/<annotation_id>")
def annotation_experiments_id(experiment_type, annotation_id):
    data = {
        'hits': [ ]
    };
    status = 200;
    if experiment_type is 'GeneExpression':
        for opengdc_obj in query_db('select * from opengdc_annotation_geneexpression where ensembl_gene_id=\''+annotation_id+'\';'):
            res = { };
            res['chrom'] = opengdc_obj[0];
            res['start_position'] = opengdc_obj[1];
            res['end_position'] = opengdc_obj[2];
            res['strand'] = opengdc_obj[3];
            res['ensembl_gene_id'] = opengdc_obj[4];
            res['entrez_gene_id'] = opengdc_obj[5];
            res['gene_symbol'] = opengdc_obj[6];
            data['hits'].append(res);
    elif experiment_type.startswith('HumanMethylation'):
        for opengdc_obj in query_db('select * from opengdc_annotation_humanmethylation where platform=\''+experiment_type+'\' and composite_element_ref=\''+annotation_id+'\';'):
            res = { };
            res['chrom'] = opengdc_obj[0];
            res['start_position'] = opengdc_obj[1];
            res['end_position'] = opengdc_obj[2];
            res['strand'] = opengdc_obj[3];
            res['composite_element_ref'] = opengdc_obj[4];
            res['gene_symbol'] = opengdc_obj[5];
            res['entrez_gene_id'] = opengdc_obj[6];
            res['gene_type'] = opengdc_obj[7];
            res['ensembl_transcript_id'] = opengdc_obj[8];
            res['position_to_tss'] = opengdc_obj[9];
            res['all_gene_symbols'] = opengdc_obj[10];
            res['all_entrez_gene_ids'] = opengdc_obj[11];
            res['all_gene_types'] = opengdc_obj[12];
            res['all_ensembl_transcript_ids'] = opengdc_obj[13];
            res['all_positions_to_tss'] = opengdc_obj[14];
            res['cgi_coordinate'] = opengdc_obj[15];
            res['feature_type'] = opengdc_obj[16];
            res['platform'] = opengdc_obj[17];
            data['hits'].append(res);
    if not data['hits']:
        data['message'] = "The specified tumor_name does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;
