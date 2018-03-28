#https://stackoverflow.com/a/36678789

from flask import Blueprint
blueprint = Blueprint('blueprint', __name__)

from .basics import *
from .data import *
from .meta import *
from .annotation import *
