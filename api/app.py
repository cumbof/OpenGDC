import os, datetime, logging, ConfigParser
from logging.handlers import RotatingFileHandler

from flask import Flask
from db import *
from routes import *

if __name__ == '__main__':
    # load config
    config = ConfigParser.ConfigParser();
    config.read("./config.ini");
    # create log dir if it does not exist
    log_dir = str(config.get("APP", "log_dir"));
    if not os.path.exists(log_dir):
        os.makedirs(log_dir);
    
    # initialize app
    # http://blog.luisrei.com/articles/flaskrest.html
    app = Flask(__name__);
    # register routes Blueprint
    app.register_blueprint(blueprint); # retrieve and register routes from __init__py in routes dir

    # init DB
    initializeDB();
    # if debug=True -> it will print out possible Python errors on the web page
    app_debug = str(config.get("APP", "debug"));
    app_host = str(config.get("APP", "host"));
    app_port = int(config.get("APP", "port"));
    if app_debug.lower().strip() == "true":
        app_debug = True;
    else:
        app_debug = False;
    # if the service is in production [debug=False]
    if not app_debug:
        # create log directory
        main_log_dir = config.get("APP", "log_dir");
        # log dir name: (year)(month)(day)(hour)(minute)(second)
        timestamp = datetime.datetime.now().strftime('%Y%m%d%H%M%S');
        current_log_dir = os.path.join(main_log_dir, timestamp);
        if not os.path.exists(current_log_dir):
            os.makedirs(current_log_dir);
        # init log handler
        log_file_path = os.path.join(current_log_dir, 'app.log');
        log_max_bytes = int(config.get("APP", "log_max_bytes"));
        log_backup_count = int(config.get("APP", "log_backup_count"));
        log_handler = RotatingFileHandler(log_file_path, maxBytes=log_max_bytes, backupCount=log_backup_count);
        log_handler.setFormatter(logging.Formatter('%(asctime)s %(levelname)s: %(message)s [in %(pathname)s:%(lineno)d]'));
        log_handler.setLevel(logging.INFO);
        app.logger.addHandler(log_handler);
        app.logger.setLevel(logging.INFO);
        app.logger.info(application() + ' ['+release()+'] startup');
    # start Flask
    app.secret_key = os.urandom(16);
    app.run(debug=app_debug, host=app_host, port=app_port);
    