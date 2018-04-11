library(tm)
library(gdata)
library("corpcor")
transitionfile = read.csv(file = '/media/toshibasecond/healthmonitoringovertime/transitionmatrix/356/356' ,sep = ',',colClasses = c("NULL",rep("numeric",52)))
transitionmatrix = as.matrix(transitionfile)
diagonalentries = diag(transitionmatrix)
nondiagonalentries = transitionmatrix[row(transitionmatrix)!=col(transitionmatrix)]
pos = which(diagonalentries>=0.5)