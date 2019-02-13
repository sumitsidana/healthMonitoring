#!/bin/bash

# Download all the input files here: https://1drv.ms/f/s!Alr4JS0ifh7GgfJa0mnyU7NaKeckuQ
# Send email to sidana.sumit@gmail.com, if something fails.

T="$(date +%s)"
javac -cp createTATAMInput/stanford-corenlp-3.3.1.jar:createTATAMInput/stanford-corenlp-3.5.0.jar:createTATAMInput/stanford-corenlp-3.2.0-models.jar:.:createTATAMInput/joda-time-2.5.jar  createTATAMInput/createTATAMInput/CreateTATAMInput.java createTATAMInput/createTATAMInput/StanfordLemmatizer.java

java -cp ./:createTATAMInput/:createTATAMInput/stanford-corenlp-3.3.1.jar:createTATAMInput/stanford-corenlp-3.5.0.jar:createTATAMInput/stanford-postagger-full-2014-01-04/:createTATAMInput/stanford-corenlp-3.2.0-models.jar:createTATAMInput/joda-time-2.5.jar createTATAMInput.CreateTATAMInput stopwords.txt DataSets/ healthMajorAreas

Output: InputToTATAM.static:

javac -cp createTATAMInput/joda-time-2.5.jar createTATAMInput/createTATAMInput/WriteTimeStamps.java
java -cp ./:createTATAMInput:createTATAMInput/joda-time-2.5.jar createTATAMInput.WriteTimeStamps months healthMajorAreas

# healthMajorAreas changes with new tweets. The goal is to be able to run it with new tweets


# Output: months

javac createTATAMInput/createTATAMInput/MergeTimeStampsWInput.java
java -cp .:createTATAMInput/ createTATAMInput.MergeTimeStampsWInput months InputToTATAM.static
# Output: InputToTATAMmonths
echo "Executing tatam now"
javac -cp commons-math4-4.0-SNAPSHOT.jar tatam/src/main/java/tatam/*.java
java -cp commons-math4-4.0-SNAPSHOT.jar:./tatam/src/main/java/ tatam.LearnTopicModel -model atam -Z 25 -A 25 -iters 5000 -input tatam_months/InputToTATAMmonths

T="$(($(date +%s)-T))"
echo "Time in seconds: ${T}"
echo " Writing bag of words for ailments in tatam"
python CalculationDistributionsInferences.py InputToATAM5000.assign > output_tatam.txt

echo "now look at tatam_output.txt and annotate health-related topics inferred by tatam"
