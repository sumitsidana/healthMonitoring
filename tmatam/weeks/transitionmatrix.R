library(tm)
library(gdata)
library("corpcor")
regions = list.dirs(path = "/media/toshibasecond/healthmonitoringovertime/training" , full.names = TRUE,recursive = FALSE)
for(r in regions){
  regionName = basename(r)
  trainingFolder = dirname(r)
  regionsFolder = dirname(trainingFolder)
  transitionsMatrixFolder = paste(regionsFolder,'/transitionmatrix',sep = "")
  transitionPath = paste(transitionsMatrixFolder,'/',regionName,sep = "")
  dir.create(path = paste(transitionsMatrixFolder,'/',regionName,sep = ""),
             showWarnings = TRUE,recursive = FALSE,mode = "0777")
  
  
  
  #     trainfile = read.csv(file = paste(t,"/ailments/trainfile",sep = ""), sep = ',',header =  FALSE)
  #     testfile = read.csv(file = paste(t,"/ailments/testfile",sep = ""), sep = ',',header =  FALSE)
  trainfile = read.csv(file = paste(r,"/topics/trainfile",sep = ""), sep = ',',header =  FALSE)
  testfile = read.csv(file = paste(r,"/topics/testfile",sep = ""), sep = ',',header =  FALSE)
  dmtrain = as.matrix(trainfile)
  dmtest = as.matrix(testfile)
  T = pseudoinverse(t(dmtrain) %*% dmtrain)%*%t(dmtrain)%*%dmtest
  boundaryname = basename(r)
  write.csv(T,paste(transitionPath,file='/',boundaryname,sep = ""))
  
}
