import csv
from sys import maxsize
from matplotlib import pyplot as plt 
import numpy as np  
   
surfaceArray = []
binArray = []
filename = '../edge_data/edgeDetectResults.csv'
numCells = -1


with open(filename, 'r') as csvfile:
    datareader = csv.reader(csvfile)
    next(datareader)
    for row in datareader:
        surfaceArray.append(int(row[3]))
        numCells += 1

a = np.array(surfaceArray) 
minSize = np.amin(a)
maxSize = np.amax(a)
mean = np.mean(a)
std = np.std(a)

plt.hist(a, bins = 75, color = "skyblue")

textstr = 'Min=%.2f | Max=%.2f | Mean=%.2f | STD=%.2f\n'%(minSize, maxSize, mean, std)
# print textstr
plt.text(0, -13, textstr)
plt.subplots_adjust(bottom=0.2)

plt.xlabel('Selected Surface Area')
plt.ylabel('Frequency')
plt.title("Outlier Analysis") 
plt.show()
