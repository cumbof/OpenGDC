from flask import Response, redirect
from . import blueprint

import json, ConfigParser

# load config
config = ConfigParser.ConfigParser();
config.read("./config.ini");

app_name = str(config.get("APP", "name"));
app_version = str(config.get("APP", "version"));

@blueprint.route("/routes")
def routes():
    data = {
        'application': app_name,
        'release': app_version,
        'routes': {
            'api': [
                '/api/documentation',
                '/api/version'
            ],
            'data': [
                '/data/_all',
                '/data/id/<opengdc_id>',
                '/data/tumor/<tumor_name>',
                '/data/tumor/_list',
                '/data/experiment/<experiment_type>',
                '/data/experiment/<experiment_type>/_header',
                '/data/experiment/_list'
            ],
            'meta': [
                '/meta/_all',
                '/meta/_dictionary',
                '/meta/id/<opengdc_id>',
                '/meta/id/<opengdc_id>/attribute/<meta_attribute>',
                '/meta/attribute/<meta_attribute>'
            ],
            'annotation': [
                'annotation/_experiments',
                'annotation/<experiment_type>/_all',
                'annotation/<experiment_type>/<annotation_id>'
            ]
        }
    };
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;

@blueprint.route("/api/documentation")
def documentation():
    return redirect("https://opengdc.docs.apiary.io/", code=302);

@blueprint.route("/api/version")
def api_version():
    data = {
        'version': app_version
    }
    js = json.dumps(data, indent=4, sort_keys=True);
    resp = Response(js, status=200, mimetype='application/json');
    return resp;
