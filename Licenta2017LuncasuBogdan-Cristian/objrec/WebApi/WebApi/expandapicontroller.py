from datetime import datetime
from flask import jsonify,request
from WebApi import app
from models import Token,User,Objects
from flask_cors import cross_origin
from database import db_session
import os
from sys import path as pylib
pylib += [os.path.abspath(r'../Licenta')]
import CreateObject
def loggedIn(token):
    token=Token.query.filter(Token.token==token).first()
    if token is not None and token.tokentime>datetime.now():
        return token
    return None

def addobject(content):
    if 'token' in content and 'archive' in content and 'label' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            ziparchive=content['archive']
            object=content['label']
            creator=CreateObject.CreateObject(ziparchive,token.user[0].username,object)
            feedback=creator.create()
            label=Objects.query.filter(Objects.object_label==object).first()
            if feedback is True:
                if label is None:
                    object=Objects(object)
                    object.user=token.user[0]
                    db_session.add(object)
                    db_session.commit()
                return "Object added"
            return "Object can not be added"
    return None

@app.route('/image/objects/add',methods=['POST'])
@cross_origin()
def addobjects():
    content=request.json
    reply=addobject(content)
    
    if reply is not None:
        return jsonify({"message":reply}),200
    return jsonify({"details":"Wrong data"}),400

@app.route('/image/objects',methods=['POST'])
@cross_origin()
def getobjects():
    content=request.json
    if 'token' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            user=token.user[0]
            objects=[]
            for obj in user.objects:
                objects.append({
                    "Id":obj.id,
                    "Label":obj.object_label
                    })
            return jsonify({"data":objects}),200
        return jsonify({"details":"Invalid token"}),400
    return jsonify({"details":"Wrong data"}),400

@app.route('/image/objects/delete',methods=['POST'])
@cross_origin()
def deleteobject():
    content=request.json
    if 'token' in content and 'object_id' in content:
        token=content['token']
        token=loggedIn(token)
        if token is not None:
            object_id=content['object_id']
            user=token.user[0]
            object=Objects.query.filter(Objects.id==object_id and Objects.developer_id==user.id).first()
            if object is not None:
                CreateObject.deleteObject(user.username,object.object_label)
                db_session.delete(object)
                db_session.commit()
                return jsonify({"details":"Object Deleted"}),200
            return jsonify({"details":"Invalid object"}),404
        return jsonify({"details":"Not logged in"}),401
    return jsonify({"details":"Wrong data"}),400