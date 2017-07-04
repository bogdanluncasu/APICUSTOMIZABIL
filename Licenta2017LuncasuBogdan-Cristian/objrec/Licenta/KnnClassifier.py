import os
import math
import pickle
import cv2
from pathlib import Path
import numpy as np

class KnnClassifier:
    def __init__(self,label,username=""):
        os.chdir("../Licenta")
        self.flatten_histograms=[]
        self.label=label
        self.username=username


    def trainImage(self,imageName,label):
        image = cv2.imread(imageName)
        print(imageName)
        hist = cv2.calcHist([image], [0, 1, 2], None, [16, 16, 16],
                            [0, 256, 0, 256, 0, 256])
        self.flatten_histograms.append((hist.flatten(),label))

    def trainKnn(self):
        for image_name in os.listdir(self.label):
                try:
                    self.trainImage(self.username+self.label+"/"+
                                    image_name,self.label)
                except cv2.error:
                    pass
        for image_name in os.listdir("not"+self.label):
                try:
                    self.trainImage(self.username+"not"+self.label+
                                    "/"+image_name,"not "+self.label)
                except cv2.error:
                    pass
        with open("training_data_"+self.username+self.label+".data","wb") as td:
            pickle.dump(self.flatten_histograms,td,protocol=2)

    def distance(self,v1,v2):
        d=0
        for i,j in zip(v1,v2):
            d+=math.pow(i-j,2)
        return math.sqrt(d)

    def classify(self,image_test,imageTest=None):
        if imageTest is None:
            imageTest = cv2.imread(image_test)
        hist = cv2.calcHist([imageTest], [0, 1, 2], None, [16, 16, 16],
                            [0, 256, 0, 256, 0, 256])
        flatten_histogram=hist.flatten()

        min=10000000
        label=""
        for training_data in self.flatten_histograms:
            tempdist=self.distance(training_data[0],flatten_histogram)
            if(tempdist<=min):
                min=tempdist
                label=training_data[1]
        print(min)
        return label

    def loadTrainData(self):
        path = "training_data_"+self.username+self.label+".data"
        my_file = Path(path)
        if not my_file.exists():
            self.trainKnn();
        with open(path, 'rb') as input:
            self.flatten_histograms = pickle.load(input)

