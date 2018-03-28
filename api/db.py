import os, sqlite3
import ConfigParser

config = ConfigParser.ConfigParser();
config.read("./config.ini");
db_schema_file_path = config.get("DB", "db_schema_file_path");
db_data_file_path = config.get("DB", "db_data_file_path");
db_file_path = config.get("DB", "db_file_path");

def query_db(query, args=(), one=False):
    with sqlite3.connect(db_file_path) as conn:
        cursor = conn.cursor();
        cursor.execute(query, args);    
        rv = cursor.fetchall()
        cursor.close()
        return (rv[0] if rv else None) if one else rv

def importSQL():
    with sqlite3.connect(db_file_path) as conn:
        with open(db_data_file_path, 'rt') as f:
            data = f.read();
            conn.executescript(data);

def initializeDB():
    db_is_new = not os.path.exists(db_file_path);
    if db_is_new:
        with sqlite3.connect(db_file_path) as conn:
            print 'Creating schema';
            with open(db_schema_file_path, 'rt') as f:
                schema = f.read();
                conn.executescript(schema);
                if os.path.exists(db_data_file_path):
                    print 'Populating DB';
                    importSQL();
