from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
import os

engine = create_engine('postgres://cfvmuucfgmvffs:21cf655aa5c7c0390deb1c4a684a52d36f4445599d6e3997e817ecc33bccf744@ec2-54-75-229-201.eu-west-1.compute.amazonaws.com:5432/d3maulac2ptgen', convert_unicode=True)
db_session = scoped_session(sessionmaker(autocommit=False,autoflush=False,bind=engine))
Base = declarative_base()
Base.query = db_session.query_property()

def init_db():
    import models
    Base.metadata.create_all(bind=engine)

