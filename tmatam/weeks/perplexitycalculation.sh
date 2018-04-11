#!/bin/bash

mkdir -p data/perplexity
mkdir -p data/perplexity/atam
mkdir -p data/perplexity/transitionmatrix

echo 'dividing dataset according to predicted top season'
javac -cp binaries/joda-time-2.5.jar perplexity/TopRankedBoundries.java
java -cp .:binaries/joda-time-2.5.jar perplexity.TopRankedBoundries data/predictedseasonsv1 data/activeregions data/perplexity/toptwoseasonsv1

echo 'dividing data of all seasons into train and test season'
javac -cp binaries/joda-time-2.5.jar perplexity/DivideDataset.java
java -cp .:binaries/joda-time-2.5.jar perplexity.DivideDataset data/perplexity/toptwoseasonsv1 data/perplexity/atam/train data/perplexity/atam/test

echo 'running ATAM'
cp -r data/perplexity/atam/train perplexity/ATAM/train
cd perplexity/ATAM/
./runATAM.sh
cd -

echo 'copying train file'
cp -r perplexity/ATAM/train data/perplexity/

echo 'copying files to a different directory'
cd perplexity/
./operate.sh
cd -

echo 'converting inferenceds to distributions'
javac -cp binaries/joda-time-2.5.jar perplexity/ConvertBoundaryInferenceTopicalDistributions.java
java -cp .:binaries/joda-time-2.5.jar perplexity.ConvertBoundaryInferenceTopicalDistributions data/perplexity/topicailmentcopyfiles 25 25 topics/

echo 'copying the distributions to separate folders'
javac -cp binaries/commons-io-2.4.jar perplexity/GetBoundaryDistributions.java
java -cp .:binaries/commons-io-2.4.jar perplexity.GetBoundaryDistributions data/perplexity/topicailmentcopyfiles data/perplexity/topicailmentdistributions/

echo 'writing historical and present files'
javac perplexity/WriteBoundaryDistributions.java
java -cp . perplexity.WriteBoundaryDistributions data/perplexity/topicailmentdistributions/ data/perplexity/topicailmenttraining/

echo 'formulating transition matrix'
cd perplexity/
./transitionmatrix.R

cd -

echo 'writing word counts'
cd perplexity/
./operatewc.sh
cd -
echo 'forming inverted index'
javac perplexity/InvertedIndex.java
java -cp . perplexity.InvertedIndex data/perplexity/wordcounts data/perplexity/InvertedIndex


echo 'getting first week of the test data'
javac -cp binaries/joda-time-2.5.jar perplexity/GetFirstWeekTestData.java
java -cp .:binaries/joda-time-2.5.jar perplexity.GetFirstWeekTestData data/perplexity/atam/test data/perplexity/toptwoseasonsv1 data/perplexity/traintestweeks

echo 'getting second week of the test data'
javac -cp binaries/joda-time-2.5.jar perplexity/GetSecondWeekTestData.java
java -cp .:binaries/joda-time-2.5.jar perplexity.GetSecondWeekTestData data/perplexity/atam/test data/perplexity/toptwoseasonsv1 data/perplexity/testtestweeks

echo 'getting probability of entity given the word'
javac perplexity/ProbabilityEntityGivenWord.java
java -cp . perplexity.ProbabilityEntityGivenWord data/perplexity/InvertedIndex data/perplexity/traintestweeks 25 25 data/perplexity/entitygivenwordprobability data/stopwords.txt

echo 'getting probability of entity given tweet'
javac perplexity/ProbabilityEntityGivenTweet.java
java -cp . perplexity.ProbabilityEntityGivenTweet data/perplexity/entitygivenwordprobability data/perplexity/traintestweeks data/perplexity/entitygiventweetprobability data/stopwords.txt 25 25 data/stopwords.txt

echo 'getting probability entity given week'
javac perplexity/ProbabilityEntityGivenWeek.java
java -cp . perplexity.ProbabilityEntityGivenWeek data/perplexity/entitygiventweetprobability data/perplexity/entitygivenweekprobability

echo 'writing probability of entity given week'
javac perplexity/WriteProbabilityEntityGivenWeek.java
java -cp . perplexity.WriteProbabilityEntityGivenWeek data/perplexity/entitygivenweekprobability data/perplexity/entitygivenweektraining

echo 'predicting test week distribution using R script'
cd perplexity/
./predicttestweekdistribution.R

echo 'writing entity counts'
javac perplexity/EntityCounts.java
java -cp . perplexity.EntityCounts data/perplexity/wordcounts data/perplexity/entitycounts 25 25


echo 'predicting word probabilities using frequentist approach'
javac perplexity/PredictedWordProbabilities.java
java -cp . perplexity/PredictedWordProbabilities data/perplexity/InvertedIndex data/perplexity/entitycounts data/perplexity/testtestweeks data/stopwords.txt 25 25 data/perplexity/entitygivenweekprobabilitypredicted data/perplexity/predictedwordprobability


echo 'predicting perplexity of TMATAM when operating on granularity of weeks'
javac perplexity/MeasurePerplexityBigInteger.java perplexity/BigDecimalUtils.java
java -cp . perplexity.MeasurePerplexityBigInteger data/perplexity/predictedwordprobability data/perplexity/predictedperplexitytmatam

echo 'making directory for tmlda in data/'
mkdir -p data/perplexity/tmldaperplexity

echo 'merging train data'
javac perplexity/tmldaperplexity/MergeTrainData.java
java -cp ./perplexity/ tmldaperplexity.MergeTrainData data/perplexity/atam/train data/perplexity/tmldaperplexity/

echo 'running lda'
cd perplexity/tmldaperplexity/peter
javac -cp commons-math-2.1.jar *.java
java -cp commons-math-2.1.jar:. LearnTopicModel -model lda -Z 50 -iters 1000 -input ../../../data/perplexity/tmldaperplexity/mergedtrainfile

cd -

echo 'merging tweet ids'
javac perplexity/tmldaperplexity/MergedTweetId.java
java -cp .:perplexity/ tmldaperplexity.MergedTweetId data/perplexity/atam/train data/perplexity/tmldaperplexity/

echo 'merging tweets to their respective regions'
javac perplexity/tmldaperplexity/TweetToRegion.java
java -cp .:perplexity/ tmldaperplexity.TweetToRegion data/perplexity/atam/train data/perplexity/tmldaperplexity/

echo 'merging tweets with their region along with their lda based inferences'
paste -d '\t' data/perplexity/tmldaperplexity/tweetregion data/perplexity/tmldaperplexity/mergedtrainfile.assign > data/perplexity/tmldaperplexity/tweetregioninference

echo 'converting inference to distributions'
javac perplexity/tmldaperplexity/ConvertInferenceToDistribution.java
java -cp .:perplexity/ tmldaperplexity.ConvertInferenceToDistribution data/perplexity/tmldaperplexity/tweetregioninference 50 data/perplexity/tmldaperplexity/tweetregiondistribution

echo 'creating train and test files'
javac perplexity/tmldaperplexity/CreateTrainTestFiles.java
java -cp .:perplexity/ tmldaperplexity.CreateTrainTestFiles data/perplexity/tmldaperplexity/tweetregiondistribution data/perplexity/tmldaperplexity/train data/perplexity/tmldaperplexity/test

echo 'computing transitions'
cd perplexity/tmldaperplexity
./computetransition.R
cd -

echo 'writing word counts for lda'
python perplexity/tmldaperplexity/CalculateLDADistributionsInferences.py data/perplexity/tmldaperplexity/mergedtrainfile.assign >data/perplexity/tmldaperplexity/wordcounts

echo 'merging first week of test data'
javac perplexity/tmldaperplexity/TestTweetstoTrainMerge.java 
java -cp .:perplexity/ tmldaperplexity.TestTweetstoTrainMerge data/perplexity/traintestweeks data/perplexity/tmldaperplexity/


echo 'merging second week of test data'
javac perplexity/tmldaperplexity/TestTweetsToTestMerge.java
java -cp .:perplexity/ tmldaperplexity.TestTweetsToTestMerge data/perplexity/testtestweeks data/perplexity/tmldaperplexity/

echo 'creating inverted index'
javac perplexity/tmldaperplexity/InvertedIndex.java
java -cp .:perplexity/ tmldaperplexity.InvertedIndex data/perplexity/tmldaperplexity/wordcounts data/perplexity/tmldaperplexity/

echo 'finding probabilities of topics given word'
javac perplexity/tmldaperplexity/ProbabilityEntityGivenWord.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenWord data/perplexity/tmldaperplexity/InvertedIndex data/perplexity/tmldaperplexity/mergedtraintestfile 50 data/perplexity/tmldaperplexity/probabilityentitygivenword data/stopwords.txt

echo 'finding probabilities of tweets given word'
javac perplexity/tmldaperplexity/ProbabilityEntityGivenTweet.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenTweet data/perplexity/tmldaperplexity/probabilityentitygivenword data/perplexity/tmldaperplexity/mergedtraintestfile data/perplexity/tmldaperplexity/probabilityentitygiventweet data/stopwords.txt

echo 'finding probabilities of entity given week'
javac perplexity/tmldaperplexity/ProbabilityEntityGivenWeek.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenWeek data/perplexity/tmldaperplexity/probabilityentitygiventweet data/perplexity/tmldaperplexity/probabilityentitygivenweek

echo 'writing probability entity given week'
javac perplexity/tmldaperplexity/WriteProbabilityEntityGivenWeek.java
java -cp .:perplexity/ tmldaperplexity.WriteProbabilityEntityGivenWeek data/perplexity/tmldaperplexity/probabilityentitygivenweek data/perplexity/tmldaperplexity/trainingentitygivenweek

echo 'predicting test week probability'
cd perplexity/tmldaperplexity/
./predicttestweekdistribution.R
cd -

echo 'finding entity counts'
javac perplexity/tmldaperplexity/EntityCounts.java
java -cp .:perplexity/ tmldaperplexity.EntityCounts data/perplexity/tmldaperplexity/wordcounts data/perplexity/tmldaperplexity/entitycounts 50

echo 'predicting word probabilities'
javac perplexity/tmldaperplexity/PredictedWordProbabilities.java
java -cp .:perplexity/ tmldaperplexity.PredictedWordProbabilities data/perplexity/tmldaperplexity/InvertedIndex data/perplexity/tmldaperplexity/entitycounts data/perplexity/tmldaperplexity/mergedtesttestfile data/stopwords.txt 50 data/perplexity/tmldaperplexity/predictedprobabilityentitytestweek data/perplexity/tmldaperplexity/predictedwordprobability

echo 'Measuring perplexity'
javac perplexity/tmldaperplexity/MeasurePerplexityBigInteger.java perplexity/tmldaperplexity/BigDecimalUtils.java
java -cp .:perplexity/ tmldaperplexity.MeasurePerplexityBigInteger data/perplexity/predictedwordprobability data/perplexity/tmldaperplexity/predictedperplexitytmlda data/perplexity/tmldaperplexity/predictedwordprobability

echo 'comparing perplexities'
javac perplexity/tmldaperplexity/ComparisonPerplexities.java
java -cp .:perplexity/ tmldaperplexity/ComparisonPerplexities data/perplexity/predictedperplexitytmatam data/perplexity/tmldaperplexity/predictedperplexitytmlda






















