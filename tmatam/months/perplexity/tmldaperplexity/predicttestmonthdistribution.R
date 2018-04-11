library(tm)
library(gdata)
library("corpcor")
transitionfile = read.csv(file ="../data/perplexity/tmldaperplexity/transitionlda"
                          ,sep = ',',colClasses = c("NULL",rep("numeric",51)))
histdist = read.csv(file = "../../data/perplexity/tmldaperplexity/trainingentitygivenmonth",sep = ',',header = FALSE)
transitionmatrix = as.matrix(transitionfile)
historicalmatrix = as.matrix(histdist)  
predictedmatrix = historicalmatrix%*%transitionmatrix
predictedPath = '../../data/perplexity/tmldaperplexity'
write.csv(predictedmatrix,paste(predictedPath,file='/predictedprobabilityentitytestmonth',sep = ""))