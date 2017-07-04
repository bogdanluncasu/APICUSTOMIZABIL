import random
import math
import operator
class KMeans():
    def getCentroid(self,dim):
        centroid=[]
        for _ in range(dim):
            centroid.append(random.uniform(250,750))
        return tuple(centroid)

    def getDistance(self,point,centroid):
        sum=0
        for coordp,coordc in zip(point,centroid):
            sum+=pow((coordp-coordc),2)
        return math.sqrt(sum)
    def recalculatecentroid(self,cluster,centroid):
        if len(cluster)==0:
            return centroid
        else:
            sum=None
            for point in cluster:
                if sum is None:
                    sum=point
                else:
                    sum=tuple(map(operator.add, sum, point))
            return tuple([x / len(cluster) for x in sum])

    def kMeans(self,data,k,attempts,dim):
        centroids=[data[i] for i in range(k)]
        clusters=[[] for _ in range(k)]
        iteration=0
        while iteration<attempts:
            iteration+=1
            clusters=[[] for _ in range(k)]
            for point in data:
                mindistance=2e9
                nearcentroid=-1

                for indcentroid in range(k):
                    distance=self.getDistance(point,centroids[indcentroid])
                    if(mindistance>distance):
                        mindistance=distance
                        nearcentroid=indcentroid
                clusters[nearcentroid].append(point)
            
            for indcentroid in range(k):
                centroids[indcentroid]=self.recalculatecentroid(
                    clusters[indcentroid],centroids[indcentroid]
                    )
        return centroids,clusters