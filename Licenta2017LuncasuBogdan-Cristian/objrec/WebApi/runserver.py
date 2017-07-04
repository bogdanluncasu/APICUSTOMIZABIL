from os import environ
from WebApi import app
from flask_cors import CORS
import database
database.init_db()
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

if __name__ == '__main__':
    HOST = '0.0.0.0'
    try:
        PORT = int(environ.get('PORT', 5000))
    except ValueError:
        PORT = 5555
    app.run(HOST, PORT)
