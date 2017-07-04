import os
import base64
import zipfile
from PIL import Image
import Licenta
import shutil
path=os.path.dirname(Licenta.__file__)
os.chdir(path)
class CreateObject():
    def __init__(self, archive, username, label):
        self.archive=archive
        self.username=username
        self.label=label

    def isImage(self,filename):
        try:
            im=Image.open(filename)
            return True
        except IOError:
            return False
    def fixBadZipfile(self,zipFile):  
         f = open(zipFile, 'r+b')  
         data = f.read()  
         pos = data.rfind(b'\x50\x4b\x05\x06') 
         if (pos > 0):  
             print("Trancating file at location " + str(pos + 22)+ ".")  
             f.seek(pos + 20)  
             f.truncate()  
             f.write(b'\x00\x00')
             f.close()  


    def create(self):
        directory=self.username+"objects"
        if not os.path.exists(directory):
            os.makedirs(directory)
        labeldirectory=directory+"/"+self.label
        if not os.path.exists(labeldirectory):
            os.makedirs(labeldirectory)
        archive=base64.b64decode(self.archive)
        with open(labeldirectory+"/temp.zip","wb") as tempfile:
            tempfile.write(archive)
        self.fixBadZipfile(labeldirectory+"/temp.zip")
        archive = zipfile.ZipFile(labeldirectory+"/temp.zip", 'r')
        archivedfiles=archive.namelist()
        for file in archivedfiles:
            image_file=archive.open(file)
            if self.isImage(image_file):
                archive.extract(file, labeldirectory)
        archive.close()
        return True

def deleteObject(username,label):
    print(username+"  "+label)
    directory=username+"objects"
    if not os.path.exists(directory):
       return
    labeldirectory=directory+"/"+label
    if not os.path.exists(labeldirectory):
        return
    else:
        shutil.rmtree(labeldirectory)