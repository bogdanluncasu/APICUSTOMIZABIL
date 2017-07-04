"""
The flask application package.
"""

from flask import Flask
app = Flask(__name__)

import WebApi.usercontroller
import WebApi.authorizationcontroller
import WebApi.imageprocessingcontroller
import WebApi.objectrecognisercontroller
import WebApi.expandapicontroller
import WebApi.developercontroller