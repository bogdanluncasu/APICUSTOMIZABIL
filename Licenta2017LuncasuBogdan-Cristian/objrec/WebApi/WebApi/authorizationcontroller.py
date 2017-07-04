from datetime import datetime,timedelta
from flask import render_template,jsonify,request
from WebApi import app
from models import Token,User
from database import db_session
import random
import string
import sqlalchemy.exc as exc
from flask_cors import cross_origin

def login(content):
    if 'username' in content and 'password' in content:
        username=content['username']
        password=content['password']

        user=User.query.filter(User.username == username).first()
        if user is None:
            return user

        token=user.token

        tokenvalue=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(116))
        token.token=tokenvalue
        token.tokentime=datetime.now()+timedelta(minutes = 20)

        db_session.commit()

        return {
            "username":user.username,
            "email":user.email,
            "token":user.token.token
                }
    return None

@app.route('/auth',methods=['POST'])
@cross_origin()
def loginuser():
    content=request.json
    user=login(content)
    
    if user is not None:
        return jsonify({"user":user}),200
    return jsonify({"details":"Wrong data"}),400

