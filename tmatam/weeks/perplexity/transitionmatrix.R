#!/usr/bin/env Rscript	
library(tm)
library(gdata)
library("corpcor")
regions = list.dirs(path = "../data/perplexity/topicailmenttraining/" , full.names = TRUE,recursive = FALSE)
for(r in regions){
  regionName = basename(r)
  trainingFolder = dirname(r)
  regionsFolder = dirname(trainingFolder)
  transitionsMatrixFolder = paste(regionsFolder,'/transitionmatrix',sep = "")
  transitionPath = paste(transitionsMatrixFolder,'/',regionName,sep = "")
  dir.create(path = paste(transitionsMatrixFolder,'/',regionName,sep = ""),
             showWarnings = TRUE,recursive = FALSE,mode = "0777")
  
  timeperiod = list.dirs(path = r,full.names = TRUE,recursive = FALSE)
  
  for(t in timeperiod){
    #     trainfile = read.csv(file = paste(t,"/ailments/trainfile",sep = ""), sep = ',',header =  FALSE)
    #     testfile = read.csv(file = paste(t,"/ailments/testfile",sep = ""), sep = ',',header =  FALSE)
    trainfile = read.csv(file = paste(t,"/topics/trainfile",sep = ""), sep = ',',header =  FALSE)
    testfile = read.csv(file = paste(t,"/topics/testfile",sep = ""), sep = ',',header =  FALSE)
    dmtrain = as.matrix(trainfile)
    dmtest = as.matrix(testfile)
    T = pseudoinverse(t(dmtrain) %*% dmtrain)%*%t(dmtrain)%*%dmtest
    boundaryname = basename(t)
    write.csv(T,paste(transitionPath,file='/',boundaryname,sep = ""))
  }
}
