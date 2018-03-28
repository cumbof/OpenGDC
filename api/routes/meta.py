from flask import Response, redirect
from . import blueprint

import json

@blueprint.route("/meta/_all")
def meta_all():
    data = {
        'hits': { }
    };
    for opengdc_obj in query_db('select * from opengdc_clinical'):
        data['hits'][opengdc_obj[0]][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_biospecimen'):
        data['hits'][opengdc_obj[0]][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_manually_curated'):
        data['hits'][opengdc_obj[0]][opengdc_obj[1]] = opengdc_obj[2];
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/meta/_dictionary")
def meta_all():
    data = {
        'hits': { }
    };
    for opengdc_obj in query_db('select * from opengdc_clinical'):
        if opengdc_obj[2] not in data['hits'][opengdc_obj[1]]
            data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    for opengdc_obj in query_db('select * from opengdc_biospecimen'):
        if opengdc_obj[2] not in data['hits'][opengdc_obj[1]]
            data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    for opengdc_obj in query_db('select * from opengdc_manually_curated'):
        if opengdc_obj[2] not in data['hits'][opengdc_obj[1]]
            data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/meta/id/<opengdc_id>")
def meta_id_endpoint(opengdc_id):
    data = { 
        'hits': { }
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_clinical where opengdc_id=\''+opengdc_id+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_biospecimen where opengdc_id=\''+opengdc_id+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_manually_curated where opengdc_id=\''+opengdc_id+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    if not data['hits']:
        data['message'] = "The specified opengdc_id does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/meta/id/<opengdc_id>/attribute/<meta_attribute>")
def meta_id_attribute_endpoint(opengdc_id, meta_attribute):
    data = { 
        'hits': { }
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_clinical where opengdc_id=\''+opengdc_id+'\' and meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_biospecimen where opengdc_id=\''+opengdc_id+'\' and meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    for opengdc_obj in query_db('select * from opengdc_manually_curated where opengdc_id=\''+opengdc_id+'\' and meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]] = opengdc_obj[2];
    if not data['hits']:
        data['message'] = "The specified opengdc_id or meta_attribute does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;

@blueprint.route("/meta/attribute/<meta_attribute>")
def meta_attribute_endpoint(meta_attribute):
    data = { 
        'hits': { }
    };
    status = 200;
    for opengdc_obj in query_db('select * from opengdc_clinical where meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    for opengdc_obj in query_db('select * from opengdc_biospecimen where meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    for opengdc_obj in query_db('select * from opengdc_manually_curated where meta_attribute=\''+meta_attribute+'\''):
        data['hits'][opengdc_obj[1]].append(opengdc_obj[2]);
    if not data['hits']:
        data['message'] = "The specified opengdc_id or meta_attribute does not exist."
        status = 400;
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=status, mimetype='application/json');
    return resp;
