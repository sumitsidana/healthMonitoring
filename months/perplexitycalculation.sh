#!/bin/bash

# change package names in classes from perplexitymonths to perplexity

mkdir -p data/perplexity
mkdir -p data/perplexity/atam
mkdir -p data/perplexity/transitionmatrix

javac -cp binaries/joda-time-2.5.jar perplexity/TopRankedBoundries.java
java -cp .:binaries/joda-time-2.5.jar perplexity.TopRankedBoundries data/perplexity/predictedseasonsv1 data/activeregionnames data/perplexity/toptwoseasonsv1

javac -cp binaries/joda-time-2.5.jar perplexity/DivideDataset.java
java -cp .:binaries/joda-time-2.5.jar perplexity.DivideDataset data/perplexity/toptwoseasonsv1/ data/perplexity/atam/train data/perplexity/atam/test

cp -r data/atam/train data/perplexity/

cd perplexity/ATAM/
sh runATAM.sh
cd -

sh perplexity/operatediff.sh

javac -cp binaries/commons-io-2.4.jar  perplexity/PerplMonthsValidBoundaries.java
java -cp . perplexity.PerplMonthsValidBoundaries data/perplexity/atam/train data/perplexity/topicailmentcopyfiles/ data/perplexity/atamoutput/

javac -cp binaries/joda-time-2.5.jar perplexity/ConvertBoundaryInferenceTopicalDistributions.java
java -cp .:binaries/joda-time-2.5.jar perplexity.ConvertBoundaryInferenceTopicalDistributions data/perplexity/atamoutput 25 25 topics

javac -cp binaries/commons-io-2.4.jar perplexity/GetBoundaryDistributions.java
java -cp .:binaries/commons-io-2.4.jar perplexity/GetBoundaryDistributions data/perplexity/atamoutput data/perplexity/topicailmentdistributions/

javac perplexity/WriteBoundaryDistributions.java
java -cp . perplexity/WriteBoundaryDistributions data/perplexity/topicailmentdistributions/ data/perplexity/topicailmenttraining/

cd perplexity/
./transitionmatrix.R
cd -

mkdir -p data/validinferences
java -cp . perplexity.PerplMonthsValidBoundaries data/perplexity/atam/train data/perplexity/topicailmentcopyfiles/ data/perplexity/validinferences/

cd perplexity
sh perplexity/operate.sh

cd -

javac perplexity/InvertedIndex.java
java -cp . perplexity/InvertedIndex data/perplexity/wordcounts data/perplexity/InvertedIndex

javac -cp binaries/joda-time-2.5.jar perplexity/GetFirstMonthTestData.java
java -cp .:binaries/joda-time-2.5.jar perplexity/GetFirstMonthTestData data/perplexity/atam/test data/perplexity/toptwoseasonsv1/ data/perplexity/traintestmonths

javac -cp binaries/joda-time-2.5.jar perplexity/GetSecondMonthTestData.java
java -cp .:binaries/joda-time-2.5.jar perplexity/GetSecondMonthTestData data/perplexity/atam/test data/perplexity/toptwoseasonsv1/ data/perplexity/testtestmonths

javac perplexity/ProbabilityEntityGivenWord.java
java -cp . perplexity.ProbabilityEntityGivenWord data/perplexity/InvertedIndex data/perplexity/traintestmonths 25 25 data/perplexity/entitygivenwordprobability/ data/stopwords.txt

javac perplexity/ProbabilityEntityGivenTweet.java
java -cp . perplexity.ProbabilityEntityGivenTweet data/perplexity/entitygivenwordprobability/ data/perplexity/traintestmonths data/perplexity/entitygiventweetprobability/ data/stopwords.txt 25 25 data/stopwords.txt

javac perplexity/ProbabilityEntityGivenMonth.java
java -cp . perplexity.ProbabilityEntityGivenMonth data/perplexity/entitygiventweetprobability/ data/perplexity/entitygivenmonthprobability/

javac perplexity/WriteProbabilityEntityGivenMonth.java
java -cp . perplexity.WriteProbabilityEntityGivenMonth data/perplexity/entitygivenmonthprobability/ data/perplexity/entitygivenmonthtraining/

cd perplexity/
./predicttestweekdistribution.R

javac perplexity/EntityCounts.java
java -cp . perplexity.EntityCounts data/perplexity/wordcounts data/perplexity/entitycounts 25 25

javac perplexity/PredictedWordProbabilities.java
java -cp . perplexity.PredictedWordProbabilities data/perplexity/InvertedIndex data/perplexity/entitycounts data/perplexity/testtestmonths data/stopwords.txt 25 25 data/perplexity/entitygivenmonthprobabilitypredicted data/perplexity/predictedwordprobability

javac perplexity/MeasurePerplexityBigInteger.java perplexity/BigDecimalUtils.java
java -cp . perplexity/MeasurePerplexityBigInteger data/perplexity/predictedwordprobability data/perplexity/predictedperplexitybigdecimal/

mkdir -p data/perplexity/tmldaperplexity

javac perplexity/tmldaperplexity/MergeTrainData.java
java -cp .:perplexity/ tmldaperplexity.MergeTrainData data/perplexity/atam/train data/perplexity/tmldaperplexity/

cd perplexity/tmldaperplexity/peter
javac -cp commons-math-2.1.jar *.java
java -cp commons-math-2.1.jar:. LearnTopicModel -model lda -Z 50 -iters 1000 -input ../../../data/perplexity/tmldaperplexity/mergedtrainfile

cd -

javac perplexity/tmldaperplexity/MergedTweetId.java
java -cp .:perplexity/ tmldaperplexity.MergedTweetId data/perplexity/atam/train data/perplexity/tmldaperplexity/

javac perplexity/tmldaperplexity/TweetToRegion.java
java -cp .:perplexity/ tmldaperplexity.TweetToRegion data/perplexity/atam/train data/perplexity/tmldaperplexity/

paste -d '\t' data/perplexity/tmldaperplexity/tweetregion data/perplexity/tmldaperplexity/mergedtrainfile.assign > data/perplexity/tmldaperplexity/tweetregioninference

javac perplexity/tmldaperplexity/ConvertInferenceToDistribution.java
java -cp .:perplexity/ tmldaperplexity.ConvertInferenceToDistribution data/perplexity/tmldaperplexity/tweetregioninference data/perplexity/tmldaperplexity/tweetregiondistribution

javac perplexity/tmldaperplexity/CreateTrainTestFiles.java
java -cp .:perplexity/ tmldaperplexity.CreateTrainTestFiles data/perplexity/tmldaperplexity/tweetregiondistribution data/perplexity/tmldaperplexity/train data/perplexity/tmldaperplexity/test

cd perplexity/tmldaperplexity
./computetransition.R
cd -

python perplexity/tmldaperplexity/CalculateLDADistributionsInferences.py data/perplexity/tmldaperplexity/mergedtrainfile.assign >data/perplexity/tmldaperplexity/wordcounts

javac perplexity/tmldaperplexity/TestTweetstoTrainMerge.java
java -cp .:perplexity/ tmldaperplexity.TestTweetstoTrainMerge data/perplexity/traintestmonths data/perplexity/tmldaperplexity/

javac perplexity/tmldaperplexity/TestTweetsToTestMerge.java
java -cp .:perplexity/ tmldaperplexity.TestTweetsToTestMerge data/perplexity/testtestmonths data/perplexity/tmldaperplexity/

javac perplexity/tmldaperplexity/InvertedIndex.java
java -cp .:perplexity/ tmldaperplexity.InvertedIndex data/perplexity/tmldaperplexity/wordcounts data/perplexity/tmldaperplexity/InvertedIndex

javac perplexity/tmldaperplexity/ProbabilityEntityGivenWord.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenWord data/perplexity/tmldaperplexity/InvertedIndex data/perplexity/tmldaperplexity/mergedtraintestfile 50 data/perplexity/tmldaperplexity/probabilityentitygivenword data/stopwords.txt

javac perplexity/tmldaperplexity/ProbabilityEntityGivenTweet.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenTweet data/perplexity/tmldaperplexity/probabilityentitygivenword data/perplexity/tmldaperplexity/mergedtraintestfile data/perplexity/tmldaperplexity/probabilityentitygiventweet data/stopwords.txt

javac perplexity/tmldaperplexity/ProbabilityEntityGivenMonth.java
java -cp .:perplexity/ tmldaperplexity.ProbabilityEntityGivenMonth data/perplexity/tmldaperplexity/probabilityentitygiventweet data/perplexity/tmldaperplexity/probabilityentitygivenmonth

javac perplexity/tmldaperplexity/WriteProbabilityEntityGivenMonth.java
java -cp .:perplexity/ tmldaperplexity.WriteProbabilityEntityGivenMonth data/perplexity/tmldaperplexity/trainingentitygivenmonth

cd perplexity/tmldaperplexity/
./predicttestweekdistribution.R
cd -

javac perplexity/tmldaperplexity/EntityCounts.java
java -cp .:perplexity/ tmldaperplexity/EntityCounts data/perplexity/tmldaperplexity/wordcounts data/perplexity/tmldaperplexity/entitycounts 50

javac perplexity/tmldaperplexity/PredictedWordProbabilities.java
java -cp .:perplexity/ tmldaperplexity.PredictedWordProbabilities data/perplexity/tmldaperplexity/InvertedIndex data/perplexity/tmldaperplexity/entitycounts data/perplexity/tmldaperplexity/mergedtesttestfile data/stopwords.txt 50 data/perplexity/tmldaperplexity/predictedprobabilityentitytestmonth data/perplexity/tmldaperplexity/predictedwordprobability

javac perplexity/tmldaperplexity/MeasurePerplexityBigInteger.java perplexity/tmldaperplexity/BigDecimalUtils.java
java -cp .:perplexity/ tmldaperplexity.MeasurePerplexityBigInteger data/perplexity/predictedwordprobability data/perplexity/tmldaperplexity/predictedperplexitytmlda data/perplexity/tmldaperplexity/predictedwordprobability

javac perplexity/tmldaperplexity/ComparisonPerplexities.java
java -cp .:perplexity/ tmldaperplexity.ComparisonPerplexities data/perplexity/predictedperplexitybigdecimal data/perplexity/tmldaperplexity/predictedperplexitytmlda
























