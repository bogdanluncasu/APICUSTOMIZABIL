from database import db_session
from sqlalchemy import Column, Integer, String, DateTime,ForeignKey
from database import Base
from sqlalchemy.orm import relationship
from WebApi import app
#
#////
#
@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()

class Token(Base):
    __tablename__='token'
    id = Column(Integer, primary_key=True)
    token = Column(String(128), unique=True)
    tokentime = Column(DateTime)
    user = relationship("User")
    def __init__(self, token=None, tokentime=None):
        self.token = token
        self.tokentime = tokentime

class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    username = Column(String(50), unique=True)
    email = Column(String(120), unique=True)
    password = Column(String(50), unique=False)
    token_id = Column(Integer,ForeignKey('token.id'),unique=True)
    token = relationship("Token")
    objects = relationship("Objects", back_populates="user")
    keys = relationship("Developer", back_populates="user")
    def __init__(self, username=None, email=None,password=None):
        self.username = username
        self.email = email
        self.password = password


class Developer(Base):
    __tablename__ = 'developers'
    id = Column(Integer, primary_key=True)
    developer_key = Column(String(120),unique=True)
    developer_id = Column(Integer,ForeignKey('users.id'))
    user = relationship("User")

    def __init__(self, developer_key=None):
        self.developer_key = developer_key

class Objects(Base):
    __tablename__ = 'objects'
    id = Column(Integer, primary_key=True)
    object_label = Column(String(120))
    developer_id = Column(Integer,ForeignKey('users.id'))
    user = relationship("User",back_populates="objects")

    def __init__(self, object_label=None):
        self.object_label = object_label