#!/bin/bash

echo "Concatenating tweets to their respective regions..."

javac seasonDetection/WriteHealthMajorAreas.java
java -cp . seasonDetection.WriteHealthMajorAreas data/tweetregion data/healthTweets data/healthMajorAreas

echo "Making one folder for each region and then making one file for each week in that region..."

javac -cp binaries/joda-time-2.5.jar seasonDetection/AggregateRegionWeek.java seasonDetection/Tweet.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.AggregateRegionWeek data/healthMajorAreas data/regionweekaggregation/

echo "Copying all those regions which have tweet post at least one week to twitterintensiveregionsdirectory/"

javac -cp binaries/commons-io-2.4.jar seasonDetection/FindActiveRegions.java
java -cp .:binaries/commons-io-2.4.jar seasonDetection.FindActiveRegions data/regionweekaggregation/ data/twitterintensiveregions

echo "Finding active region names"

javac seasonDetection/ActiveRegionNames.java
java -cp . seasonDetection.ActiveRegionNames data/twitterintensiveregions data/dump.regions data/activeregionnames

echo "Writing tweetId and tweetText sorted date wise for all regions in one file"
javac -cp binaries/joda-time-2.5.jar seasonDetection/AggregateRegion.java seasonDetection/Tweet.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.AggregateRegion data/healthMajorAreas data/regionaggregation/

echo "Copying Active regions"
javac -cp binaries/commons-io-2.4.jar seasonDetection/CopyActiveRegions.java
java -cp .:binaries/commons-io-2.4.jar seasonDetection.CopyActiveRegions data/twitterintensiveregions/ data/regionaggregation/ data/activeregions/

echo "Calculating Tweet Distribution of Active Regions"
javac seasonDetection/TweetDistributionActiveRegions.java
java -cp . seasonDetection.TweetDistributionActiveRegions data/activeregions/ data/regioncount data/dump.regions

cp -r data/activeregions/ seasonDetection/ATAM/

echo 'Changing Directory to where ATAM is'
cd seasonDetection/ATAM/

echo "running atam"
./runATAM.sh

cp -r activeregions/ ../../data/

echo 'changing directory back to the directory of script '
cd -

echo 'copy inferences drawn to different folder'
cd seasonDetection/
./operate.sh
cd -

echo 'writing active regions ids'
javac seasonDetection/ActiveRegionIds.java
java -cp . seasonDetection.ActiveRegionIds data/twitterintensiveregions data/dump.regions data/activeregionids

echo 'converting inferences into weekly distributions'
javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertInferencesIntoWeeklyDistributions.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertInferencesIntoWeeklyDistributions data/copyfiles/ 25 25 topics

echo 'copy distributions to different folder'
javac -cp binaries/commons-io-2.4.jar seasonDetection/GetDistributions.java
java -cp .:binaries/commons-io-2.4.jar seasonDetection.GetDistributions data/copyfiles/ data/distributions/

echo 'convert distributions to training files'
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

echo 'Getting top five seasons via bhattacharya distance'
javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertWeekintoDays.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertWeekintoDays data/distributiondifferencedistancessorted

javac -cp binaries/joda-time-2.5.jar seasonDetection/ConvertInferencesIntoWeeklyCounts.java
java -cp .:binaries/joda-time-2.5.jar seasonDetection.ConvertInferencesIntoWeeklyCounts data/copyfiles/ 25 25 topics data/countfiles/

javac seasonDetection/WriteDistributions.java 
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
mkdir -p data/predictedseasonsv3
javac -cp binaries/joda-time-2.5.jar seasonDetection/GetTopFive.java 
java -cp .:binaries/joda-time-2.5.jar seasonDetection.GetTopFive data/sortedcosinesimilarityweeks


