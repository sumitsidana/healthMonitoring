#!/usr/bin/env Rscript
library(tm)
library(gdata)
library("corpcor")
regionstesttrain = list.dirs(path = "../data/perplexity/entitygivenmonthtraining/" , full.names = TRUE,recursive = FALSE)
for(r in regionstesttrain){
  trainingfolder =  dirname(r)
  regionsfolder = dirname(trainingfolder)
  regionName = basename(r)
  predictedMatrixFolder = paste(regionsfolder,'/entitygivenmonthprobabilitypredicted',sep = "")
  dir.create(path = paste(predictedMatrixFolder,'/',regionName,sep = "")
             ,showWarnings = TRUE,recursive = FALSE,mode = "0777")
  predictedPath = paste(predictedMatrixFolder,'/',regionName,sep = "")
  
  boundary = list.files(path = r,full.names = TRUE,recursive = FALSE)
  for(b in boundary){
    boundaryname = basename(b)
    if(length(grep("after",boundaryname))>0)
      transitionfile = read.csv(file = paste(regionsfolder,'/transitionmatrix/',regionName,'/after',sep="")
                                ,sep = ',',colClasses = c("NULL",rep("numeric",52)))
    if(length(grep("before",boundaryname))>0) 
      transitionfile = read.csv(file = paste(regionsfolder,'/transitionmatrix/',regionName,'/before',sep="")
                                ,sep = ',',
                                colClasses = c("NULL",rep("numeric",52))) 
#     boundaryname<-gsub("T00:00:00.000\\+05:30_output_atam.txt_invertedindex_wordgivenentityprobability_entitygivenwordprobability", "", boundaryname)
#     transitionfile = read.csv(file = paste(regionsfolder,'/transitionmatrix/',regionName,'/',boundaryname,sep="")
#                               ,sep = ',',colClasses = c("NULL",rep("numeric",52)))   
    histdist = read.csv(file = b,sep = ',',header = FALSE) 
    transitionmatrix = as.matrix(transitionfile)
    historicalmatrix = as.matrix(histdist)  
    predictedmatrix = historicalmatrix%*%transitionmatrix
    write.csv(predictedmatrix,paste(predictedPath,file='/predicted_',boundaryname,sep = ""))
  }
}
