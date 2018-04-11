#!/usr/bin/env Rscript
library(tm)
library(gdata)
library(lsa)
regions = list.dirs(path = "contigencytables" , full.names = TRUE,recursive = FALSE)
for(r in regions){
  region = basename(r)
  mydata = read.csv(paste(r,'/topics/trainfile',sep=""), sep = ',',header =  FALSE) 
  regionalailmenttrends <- as.matrix(mydata)
  weeks = nrow(regionalailmenttrends)
  cosinesimilarity <- matrix(,nrow = weeks,ncol = 1)
  euclideandistance <- matrix(,nrow = weeks,ncol = 1)
  for(week in 2:weeks)
  {
    cosinesimilarity[week-1,] = cosine(regionalailmenttrends[week-1,],regionalailmenttrends[week,])
    euclideandistance[week-1] = 2*(1-cosinesimilarity[week-1])
  }
  
  ha = read.csv(paste('countfiles/',region,'/topics/topicshistorical',sep=""), sep = '|',header =  FALSE)
  nameofweeks = list()
  weeknames = matrix(unlist(ha[1]))
  number <- nrow(weeknames)
  for(j  in 1:number){
    oneweekname = weeknames[j,1]
    shortname = substr(oneweekname,4,5)
    nameofweeks[[j]]<-shortname
  }
  
  
  X11 ()
  plot(cosinesimilarity,type = 'o',xaxt='n', ann=FALSE)
  axis(1, at=1:number, lab=nameofweeks)
  box()
  dev.copy(png,filename=paste("cosinesimilaritygraphs/",region,".png",sep=""))
  dev.off ();
  graphics.off()
  merged <- cbind(nameofweeks,cosinesimilarity)
  LS.df = as.data.frame(do.call(rbind, merged))
  write.csv(merged,paste('cosinesimilarityweeks/',region,sep=""))
  
  ind <- order(cosinesimilarity)
  cosinesimilarity = sort(cosinesimilarity)
  nameofweeks<-nameofweeks[ind]
  X11 ()
  plot(cosinesimilarity,type = 'o',xaxt='n', ann=FALSE)
  axis(1, at=1:number, lab=nameofweeks)
  #   box()
  dev.copy(jpeg,filename=paste("sortedcosinesimilaritygraphs/",region,".jpeg",sep=""))
  dev.off ();
  graphics.off()
  
  
  merged <- cbind(nameofweeks,cosinesimilarity)
  LS.df = as.data.frame(do.call(rbind, merged))
  write.csv(merged,paste('sortedcosinesimilarityweeks/',region,sep=""))
  
  
}
