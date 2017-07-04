from datetime import datetime,timedelta
from flask import render_template,jsonify,request
from WebApi import app
from models import Token,User,Developer
from database import db_session
import random
import string
import sqlalchemy.exc as exc
from flask_cors import cross_origin

def loggedIn(token):
    token=Token.query.filter(Token.token==token).first()
    if token is not None and token.tokentime>datetime.now():
        return token
    return None

@app.route('/developer/keys',methods=['POST'])
@cross_origin()
def getkeys():
    content=request.json
    if 'token' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            user=token.user[0]
            keys=[]
            for key in user.keys:
                keys.append({
                    'id':key.id,
                    'key':key.developer_key
                    })
            return jsonify({"data":keys}),200
        jsonify({"details":"Not authorized"}),401
    
    return jsonify({"details":"Wrong data"}),400


@app.route('/developer/keys/generate',methods=['POST'])
@cross_origin()
def generatekey():
    content=request.json
    if 'token' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            user=token.user[0]
            if len(user.keys)>4:
                return jsonify({"details":"Limit number of keys reached"}),400
            devkey=tokenvalue=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits + string.ascii_lowercase) for _ in range(128))
            dev=Developer(devkey)
            dev.user=user
            db_session.add(dev)
            db_session.commit()
            return jsonify({"details":"Key Generated"}),200
        jsonify({"details":"Not authorized"}),401
    
    return jsonify({"details":"Wrong data"}),400


@app.route('/developer/keys/delete',methods=['POST'])
@cross_origin()
def deletekey():
    content=request.json
    if 'token' in content and 'object_id' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            object_id=content['object_id']
            user=token.user[0]
            key=Developer.query.filter(Developer.id==object_id and Developer.developer_id==user.id).first()
            if key is not None:
                db_session.delete(key)
                db_session.commit()
                return jsonify({"details":"Key Deleted"}),200
            return jsonify({"details":"Invalid key"}),404
        return jsonify({"details":"Not logged in"}),401
    return jsonify({"details":"Wrong data"}),400