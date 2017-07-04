import urllib
from urllib2 import Request as request
import numpy as np
import cv2
import random
from bs4 import BeautifulSoup;


class WebCrawler:
    def __init__(self, urls):
        self.urls=urls
        self.visited=[]
        self.imgvisited=[]

    def create_opencv_image_from_url(self,url, cv2_img_flag=0):
        r = request.urlopen(url)
        img_array = np.asarray(bytearray(r.read()), dtype=np.uint8)
        return cv2.imdecode(img_array, cv2.COLOR_BAYER_BG2BGR)

    def crawling(self,targetimages=10,label="notsport"):
        nrofimages=0
        while(len(self.urls)>0 and nrofimages<=targetimages):
            try:
                html=request.urlopen(self.urls[0]).read()
                soup=BeautifulSoup(html)
                for img in soup.findAll("img",src=True):
                    try:
                        
                        normalizedurl=urllib.parse.urljoin(self.urls[0],img["src"])
                        if normalizedurl not in self.imgvisited:
                            self.imgvisited.append(normalizedurl)
                            im=self.create_opencv_image_from_url(normalizedurl)
                            nrofimages+=1
                            cv2.imwrite(label+"/img"+str(random.randint(0,999999))+".jpg",im)

                    except:
                        pass

                for url in soup.findAll("a",href=True):
                    normalizedurl=urllib.parse.urljoin(self.urls[0],url["href"])
            
                    if False:#normalizedurl not in self.visited:
                        print(normalizedurl)
                        self.urls.append(normalizedurl)
                        self.visited.append(normalizedurl)

            except (urllib.error.URLError,urllib.error.HTTPError):
                print(self.urls[0])

            self.urls.pop(0)







