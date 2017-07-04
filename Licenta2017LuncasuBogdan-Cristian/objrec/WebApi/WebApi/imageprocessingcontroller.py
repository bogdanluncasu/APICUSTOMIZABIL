from datetime import datetime,timedelta
from flask import render_template,jsonify,request
from WebApi import app
from models import Token,User
from database import db_session
import random
import string
import sqlalchemy.exc as exc
from flask_cors import cross_origin

from sys import path as pylib
import os
pylib += [os.path.abspath(r'../Licenta')]

from KnnClassifier import KnnClassifier
import numpy as np
import cv2
import base64

def issportimage(content):
    if 'image' in content:
        image=content['image']
        knn=KnnClassifier("sport")
        knn.loadTrainData()
        image=base64.b64decode(image)

        imagename=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(116))
        with open(imagename+".jpg","wb") as f:
            f.write(image)

        img = cv2.imread(imagename+".jpg")
        label=knn.classify(None,img)
        os.remove(imagename+".jpg")
        return {"label":label}
    return None

@app.route('/image/issport',methods=['POST'])
@cross_origin()
def issport():
    content=request.json
    reply=issportimage(content)
    
    if reply is not None:
        reply=jsonify(reply)
        return reply,200
    return jsonify({"details":"Wrong data"}),400



def islabelimage(content):
    if 'image' in content and 'label' in content and 'token' in content:
        token=content['token']
        image=content['image']
        label=content['label']

        token=Token.query.filter(Token.token==token).first();

        if token is not None and token.tokentime>datetime.now():
            user=token.user
            knn=KnnClassifier(label,user.username)
            knn.loadTrainData()
            image=base64.b64decode(image)

            imagename=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(116))
            with open(imagename+".jpg","wb") as f:
                f.write(image)

            img = cv2.imread(imagename+".jpg")
            label=knn.classify(None,img)
            os.remove(imagename+".jpg")
            return {"label":label}
    return None


@app.route('/image/islabel',methods=['POST'])
@cross_origin()
def islabel():
    content=request.json
    reply=islabelimage(content)

    if reply is not None:
        reply=jsonify(reply)
        return reply,200
    return jsonify({"details":"Wrong data"}),400