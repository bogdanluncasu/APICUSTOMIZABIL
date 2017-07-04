from datetime import datetime,timedelta
from flask import render_template,jsonify,request
from WebApi import app
from models import Token,User,Developer
from database import db_session
import random
import string
import sqlalchemy.exc as exc
from flask_cors import cross_origin
import numpy as np
import cv2
import base64
from sys import path as pylib
import os
pylib += [os.path.abspath(r'../Licenta')]

from usingfeatures import ObjectRecognize

def objectrecognitionimage(content):
    if 'image' in content:
        image=content['image']
        image=base64.b64decode(image)
        object_recogniser=ObjectRecognize()
        if 'developer_key' in content:
            key=content['developer_key']
            key=Developer.query.filter(Developer.developer_key==key).first()

            if key is not None:
                user=key.user
                object_recogniser=ObjectRecognize(user.username,user.objects)
        
        imagename=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(116))
        with open(imagename+".jpg","wb") as f:
                f.write(image)

        img = cv2.imread(imagename+".jpg")
        _image,_objects=object_recogniser.recognize(img)
        os.remove(imagename+".jpg")

        response={}
        response["image"]= cv2.imencode('.jpg', img)[1].tostring()
        response["objects"]=[]
        for _object in _objects:
            obj={}
            obj["matches"]=_object[0]
            obj["object"]=_object[1]
            obj["color"]=_object[-1]
            response["objects"].append(obj)
        return response
    return None


@app.route('/image/objectrecognition',methods=['POST'])
@cross_origin()
def objectrecognition():
    content=request.json
    reply=objectrecognitionimage(content)

    try:
        if reply is not None:
            reply["image"]=reply["image"].decode('iso-8859-1')
            reply=jsonify(reply)
            return reply,200
    except Exception as e:
        return "Something is wrong",200
    return jsonify({"details":"Wrong data"}),400

