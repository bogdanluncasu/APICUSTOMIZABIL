from datetime import datetime
from flask import render_template,jsonify,request
from WebApi import app
from models import Token,User
from database import db_session
import random
import string
import sqlalchemy.exc as exc
def register(content):
    if 'username' in content and\
        'email' in content and\
        'password' in content:

        username=content['username']
        email=content['email']
        password=content['password']

        tokenvalue=''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(116))
        token=Token(tokenvalue,datetime.now())
        user=User(username,email,password)
        user.token=token
        db_session.add(token)
        db_session.add(user)
        db_session.commit()
        return {
            "username":user.username,
            "email":user.email
                }
    return None

@app.route('/users',methods=['POST'])
def users():
    content=request.json
    try:
        user=register(content)
    except exc.IntegrityError:
        return jsonify({"details":"Username or email in use"}),400

    if user is not None:
        return jsonify({"user":user}),201
    return jsonify({"details":"Wrong data","content":content}),400

