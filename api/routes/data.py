from flask import Response, redirect
from . import blueprint

import json

@blueprint.route("/data/_all")
def data_all():
    data = {
        'hits': [ ]
    };
    for opengdc_obj in query_db('select * from opengdc_data'):
        res = { };
        res['opengdc_id'] = opengdc_obj[0];
        res['tumor_abbreviation'] = opengdc_obj[1];
        res['experiment_type'] = opengdc_obj[2];
        res['data_name'] = opengdc_obj[3];
        res['data_type'] = opengdc_obj[4];
        res['md5_checksum'] = opengdc_obj[5];
        res['last_update_timestamp'] = opengdc_obj[6];
        res['data_url'] = opengdc_obj[7];
        res['source'] = opengdc_obj[8];
        data['hits'].append(res);
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/data/id/<opengdc_id>")
def data_id_endpoint(opengdc_id):
    data = { 
        'hits': [ ]
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_data where opengdc_id=\''+opengdc_id+'\''):
        res = { };
        res['tumor_abbreviation'] = opengdc_obj[1];
        res['experiment_type'] = opengdc_obj[2];
        res['data_name'] = opengdc_obj[3];
        res['data_type'] = opengdc_obj[4];
        res['md5_checksum'] = opengdc_obj[5];
        res['last_update_timestamp'] = opengdc_obj[6];
        res['data_url'] = opengdc_obj[7];
        res['source'] = opengdc_obj[8];
        data['hits'].append(res);
    if not data['hits']:
        data['message'] = "The specified opengdc_id does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/data/tumor/<tumor_name>")
def data_tumor_endpoint(tumor_name):
    data = {
        'hits': [ ]
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_data where tumor_abbreviation=\''+tumor_name+'\''):
        res = { };
        res['opengdc_id'] = opengdc_obj[0];
        res['experiment_type'] = opengdc_obj[2];
        res['data_name'] = opengdc_obj[3];
        res['data_type'] = opengdc_obj[4];
        res['md5_checksum'] = opengdc_obj[5];
        res['last_update_timestamp'] = opengdc_obj[6];
        res['data_url'] = opengdc_obj[7];
        res['source'] = opengdc_obj[8];
        data['hits'].append(res);
    if not data['hits']:
        data['message'] = "The specified tumor_name does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/data/tumor/_list")
def data_tumor_list_endpoint():
    data = { 
        'hits': []
    };
    for opengdc_obj in query_db('select distinct tumor_abbreviation from opengdc_data'):
        data['hits'].append(opengdc_obj[1]);
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/data/experiment/<experiment_type>")
def data_experiment_endpoint(experiment_type):
    data = {
        'hits': [ ]
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_data where experiment_type=\''+experiment_type+'\''):
        res = { };
        res['opengdc_id'] = opengdc_obj[0];
        res['tumor_abbreviation'] = opengdc_obj[1];
        res['data_name'] = opengdc_obj[3];
        res['data_type'] = opengdc_obj[4];
        res['md5_checksum'] = opengdc_obj[5];
        res['last_update_timestamp'] = opengdc_obj[6];
        res['data_url'] = opengdc_obj[7];
        res['source'] = opengdc_obj[8];
        data['hits'].append(res);
    if not data['hits']:
        data['message'] = "The specified tumor_name does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/data/experiment/_list")
def data_experiment_list_endpoint():
    data = { 
        'hits': []
    };
    for opengdc_obj in query_db('select distinct experiment_type from opengdc_data'):
        data['hits'].append(opengdc_obj[2]);
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/data/experiment/<experiment_type>/_header")
def data_experiment_list_endpoint(experiment_type):
    data = { 
        'hits': []
    };
    for opengdc_obj in query_db('select * from opengdc_experiment_header where experiment_type=\''+experiment_type+'\' order by header_position;'):
        res = { };
        res['position'] = opengdc_obj[2];
        res['content'] = opengdc_obj[1];
        data['hits'].append(res);
    js = json.dumps(data, indent=4);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;
