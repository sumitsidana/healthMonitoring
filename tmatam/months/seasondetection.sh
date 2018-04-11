#!/bin/bash

# paste activeregions/ along with their ATAM based inferences in data/ for code to work.

javac -cp binaries/commons-io-2.4.jar seasonDetection/RenameActiveRegions.java
java -cp .:binaries/commons-io-2.4.jar seasonDetection.RenameActiveRegions data/dump.regions data/activeregions data/activeregionsnames

echo 'copy inferences drawn to different folder'
cd seasonDetection/
./operate.sh
cd -

javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertInferencesIntoMonthlyDistributions.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertInferencesIntoMonthlyDistributions data/copyfiles 25 25 topics

javac -cp binaries/commons-io-2.4.jar seasonDetection/GetDistributions.java
java -cp .:binaries/commons-io-2.4.jar seasonDetection.GetDistributions data/copyfiles data/distributions/

javac seasonDetection/WriteDistributions.java
java -cp . seasonDetection.WriteDistributions data/distributions/ data/training/

 mkdir -p seasonDetection/distributionbhdifferences
 mkdir -p seasonDetection/distributiondifferencessorted
 mkdir -p seasonDetection/distributiondifferencedistances
 mkdir -p seasonDetection/distributiondifferencedistancessorted

cp -r data/training/ seasonDetection/
cp -r data/distributions/ seasonDetection/

cd seasonDetection
echo 'drawing graphs for bhattacharya distances'
./DistributionDifferenceCalculator.m
echo 'drawing sorted graphs for bhattacharya distances'
./topk.m
echo 'writing raw files for bhattacharya distances along with weeks'
./topkfiles.m
echo 'writing raw files for sorted weeks along with their bhattacharya distances'
./writeweekdifference.m

cp -r  distributionbhdifferences ../data/
cp -r distributiondifferencessorted ../data/
cp -r distributiondifferencedistances ../data/
cp -r distributiondifferencedistancessorted ../data/

cd -
mkdir data/predictedseasonsv1
javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertWeekintoDays.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertWeekintoDays data/distributiondifferencedistancessorted

javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertInferencesIntoMonthlyCounts.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertInferencesIntoMonthlyCounts data/copyfiles/ 25 25 topics data/countfiles/

javac  seasonDetection/WriteDistributions.java
java -cp . seasonDetection.WriteDistributions data/countfiles data/contigencytables/


 mkdir -p seasonDetection/cosinesimilaritygraphs
 mkdir -p seasonDetection/cosinesimilarityweeks
 mkdir -p seasonDetection/sortedcosinesimilaritygraphs
 mkdir -p seasonDetection/sortedcosinesimilarityweeks

cp -r data/countfiles seasonDetection/
cp -r data/contigencytables seasonDetection/

cd seasonDetection
echo 'doing everything for cosine similarity'
./cosinesimilarity.R

cp -r cosinesimilaritygraphs ../data/cosinesimilaritygraphs
cp -r cosinesimilarityweeks ../data/cosinesimilarityweeks
cp -r sortedcosinesimilaritygraphs ../data/sortedcosinesimilaritygraphs
cp -r sortedcosinesimilarityweeks ../data/sortedcosinesimilarityweeks

cd -
mkdir data/predictedseasonsv3
javac -cp binaries/joda-time-2.5.jar seasonDetection/GetTopFive.java 
java -cp .:binaries/joda-time-2.5.jar seasonDetection.GetTopFive data/sortedcosinesimilarityweeks


