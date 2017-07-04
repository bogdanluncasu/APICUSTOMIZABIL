import cv2
import numpy as np
import os
import json
import random
from pathlib import Path
print(cv2.__version__)

MIN_MATCHES_NUMBER=35

class ObjectRecognize():
    def __init__(self,username="",_objects=[]):
        self.bf=cv2.BFMatcher(cv2.NORM_HAMMING)
        self.username=username
        if username != "":
            self.objects=[_obj.object_label for _obj in _objects]
        else:
            self.objects=["RealMadrid","Barcelona","Federer","FootballBall",
            "Drogba","Neymar","LionelMessi",
            "CristianoRonaldo","TennisRacket","Ibrahimovic","Trophy","Signature"]
        try:
            self.keydescriptors=json.load(open(username+"images.dat","r"))
        except:
            self.keydescriptors={}


    def to_gray(self,color_img):
        gray = cv2.cvtColor(color_img, cv2.COLOR_BGR2GRAY)
        return gray

    def gen_orb_features(self,gray_img):
        orb = cv2.ORB_create()
        kp = orb.detect(gray_img,None)
        return orb.compute(gray_img, kp)


    def drawObject(self,_objects,image):
        for _object in _objects:
            
            _pts = np.float32([ _object[3][m.queryIdx].pt for m in _object[2] ])

            minpt = np.amin(_pts,axis=0)
            maxpt = np.amax(_pts,axis=0)
            bpt = [minpt[0],maxpt[1]]
            cpt = [maxpt[0],minpt[1]]

            pts=[minpt,cpt,maxpt,bpt]
            pts=np.float32(pts).reshape(-1,1,2)
            font = cv2.FONT_HERSHEY_SIMPLEX
            r=random.randint(0,255)
            g=random.randint(0,255)
            b=random.randint(0,255)

            _object.append((r,g,b))
            cv2.putText(image,_object[1],(int(minpt[0]),int(minpt[1])),font, .5,(r,g,b),1,cv2.LINE_AA)

        #img3 = cv2.polylines(img2,[np.int32(pts)],True,[255,0,0],3, cv2.LINE_AA)

        return image

    def drawObjectMean(self,_objects,image):
        for _object in _objects:
            
            _pts = np.float32([ _object[3][m.queryIdx].pt for m in _object[2] ])

            _actualpt=[0,0]
            for _pt in _pts:
                _actualpt[0]+=_pt[0]
                _actualpt[1]+=_pt[1]
            _actualpt[0]/=len(_pts)
            _actualpt[1]/=len(_pts)
            font = cv2.FONT_HERSHEY_SIMPLEX
            r=random.randint(0,255)
            g=random.randint(0,255)
            b=random.randint(0,255)

            _object.append((r,g,b))
            cv2.putText(image,_object[1],(int(_actualpt[0]),int(_actualpt[1])),font, .5,(r,g,b),1,cv2.LINE_AA)

        return image
		
    def drawObjectHomography(self, _objects, image):
        for _object in _objects:
            good=_object[2]
            if len(good)>MIN_MATCHES_NUMBER:
                src_pts = np.float32([ _object[3][m.queryIdx].pt for m in good ]).reshape(-1,1,2)
                dst_pts = np.float32([ _object[4][m.trainIdx].pt for m in good ]).reshape(-1,1,2)

                M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,5.0)
                matchesMask = mask.ravel().tolist()

                train_image=cv2.imread(_object[5],0)
                h,w = train_image.shape
                pts = np.float32([ [0,0],[0,h-1],[w-1,h-1],[w-1,0] ]).reshape(-1,1,2)
                dst = cv2.perspectiveTransform(pts,M)

                img2 = cv2.polylines(image,[np.int32(dst)],True,255,3, cv2.LINE_AA)
		
            else:
                print("Not enough matches are found - %d/%d" % (len(good),MIN_MATCHES_NUMBER))

    def recognize(self,image):
        cv2.GaussianBlur(image,dst=image,ksize=(1,1),sigmaX=1,borderType=0)
        img1_gray = self.to_gray(image)
        
        _objects=[]
        changed=False
        key_points_image1,descriptors_image1 = self.gen_orb_features(img1_gray)
        
        for obj in self.objects:
            print(obj)
            maxmatches=-1

            objpath=self.username+"objects/"+obj
            _obj=None
            _good=None
            _key_points_image1=None
            _key_points_image2=None
            _imagepath=None
            _path=Path(objpath)
            if not _path.exists():
                continue
            for image_name in os.listdir(objpath):
                    good=[]
                    imagepath=objpath+"/"+image_name
                    try:
                        if  imagepath not in self.keydescriptors:
                            changed=True
                            img2 = cv2.imread(imagepath)
                            img2 = cv2.GaussianBlur(img2,(3,3),0)
                            img2_gray = self.to_gray(img2)
                            
                            key_points_image2,descriptors_image2 = self.gen_orb_features(img2_gray)
                           

                            tempList=[]
                            for point in key_points_image2:
                                tempPoint = (point.pt, point.size, point.angle, point.response, point.octave, point.class_id )
                                tempList.append(tempPoint)

                            desc=np.array(descriptors_image2)
                            self.keydescriptors[imagepath]=(tempList,desc.tolist())
                        else:
                            points,des = self.keydescriptors[imagepath]
                            des=np.array(des,dtype=np.uint8)
                            pointList=[]
                            for point in points:
                                tempPoint=cv2.KeyPoint(x=point[0][0],y=point[0][1],_size=point[1], _angle=point[2], 
                                    _response=point[3], _octave=point[4], _class_id=point[5])
                                pointList.append(tempPoint)

                            key_points_image2,descriptors_image2 = pointList,des

                        matches = self.bf.match(descriptors_image1,descriptors_image2)
                        matches = sorted(matches,key=lambda x:x.distance)
                        #matches = self.flann.knnMatch(descriptors_image1,descriptors_image2,k=1)
                        for match in matches:
                          if match.distance < 50:
                            good.append(match)
                        
                        goodmatches=len(good)
                        if(goodmatches>maxmatches):
                            maxmatches=goodmatches
                            _obj=obj
                            _good=good
                            _key_points_image1=key_points_image1
                            _key_points_image2=key_points_image2
                            _imagepath=imagepath

                    except IndexError:
                        pass
                    except cv2.error:
                        pass
            print(maxmatches)
            if maxmatches>MIN_MATCHES_NUMBER:
                   _object=[maxmatches,_obj,_good,
                                _key_points_image1,_key_points_image2,_imagepath]
                   _objects.append(_object)
        if(len(_objects)>0):
            if changed:
                json.dump(self.keydescriptors,open(self.username+"images.dat", 'w'))

            image=self.drawObjectMean(_objects,image)
            return image,_objects
        else:
            return image,[]

    def recognizeStats(self,dirTest,testLabel):
        counter=0
        images=0
        for image_name in os.listdir(dirTest):
            images+=1
            label=self.recognize(dirTest+image_name)
            if(label == testLabel):
                counter+=1
        print(counter+"/"+images)



    def test_rotation(self,image1,image2):
        #cv2.GaussianBlur(image1,dst=image1,ksize=(3,3),sigmaX=0,borderType=0)
        #cv2.GaussianBlur(image2,dst=image2,ksize=(3,3),sigmaX=0,borderType=0)
        img1_gray = self.to_gray(image1)
        img2_gray = self.to_gray(image2)
        
        key_points_image1,descriptors_image1 = self.gen_orb_features(image1)
        key_points_image2,descriptors_image2 = self.gen_orb_features(image2)
        


        matches = self.bf.match(descriptors_image1,descriptors_image2)
        matches = sorted(matches, key = lambda x:x.distance)
        print(len(key_points_image2))
        good = []
        for m_n in matches:
          if m_n.distance < 100:
            good.append(m_n)
        img3 = cv2.drawMatches(image1,key_points_image1,image2,key_points_image2,matches, outImg=None)

        

#object_recognizer = ObjectRecognize("")
#object_recognizer.test_rotation(cv2.imread("test/s1.jpg"),cv2.imread("objects/Signature/4.jpg"))
#image,objects=object_recognizer.recognize(cv2.imread("test/s1.jpg"))
#cv2.imshow("Image",image)
#cv2.waitKey(0)

#object_recognizer.recognizeStats("test/testMessi/","LionelMessi")

#import ImageCrawler
#urls=["http://www.gettyimages.com/photos/Messi-face"]
#crawler=ImageCrawler.WebCrawler(urls)
#crawler.crawling(10,"objects/LionelMessi")
