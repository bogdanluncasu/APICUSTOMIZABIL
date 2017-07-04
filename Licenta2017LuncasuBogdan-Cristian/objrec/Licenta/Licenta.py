import cv2
import os
#import ImageCrawler
import pickle
from KMeans import KMeans
from KnnClassifier import KnnClassifier
import usingfeatures

def getURLS(file):
    urls=[]
    with open(file,"r") as f:
        while True:
            line=f.readline()
            if not line:
                break
            else:
                urls.append(line)

    return urls

def downloadimages():
    urls=getURLS("urlssport.txt")
    urlsnotsport=getURLS("urlsnotsport.txt")
    ImageCrawler.WebCrawler(urls).crawling(2000,"sport")
    ImageCrawler.WebCrawler(urlsnotsport).crawling(2000)


def testKnn():
    knn=KnnClassifier()
    knn.trainKnn()
    #knn.loadTrainData()

    sport=0
    notsport=0
    for i in range(3,17):
        label=knn.classify("test/"+str(i)+".jpg")
        if label == "sport":
            sport+=1
        else:
            notsport+=1
    print(sport)
    print(notsport)


#downloadimages()