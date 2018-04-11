#!/bin/bash
T="$(date +%s)"
javac -cp createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-corenlp-3.2.0-models.jar:.:  createATAMInput/CreateATAMInputStopWordsUnleashedWithLowerCaseIssue.java createATAMInput/StanfordLemmatizer.java
java -cp ./:./createATAMInput/:createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-postagger-full-2014-01-04/:createATAMInput/stanford-corenlp-3.2.0-models.jar createATAMInput.CreateATAMInputStopWordsUnleashedWithLowerCaseIssue ../stopwords.txt ../DataSets/ 
#
javac -cp commons-math-2.1.jar *.java
java -cp commons-math-2.1.jar:. LearnTopicModel -model atam -Z 25 -A 25 -iters 5000 -input ../InputToATAMUsingLemmatizer





T="$(($(date +%s)-T))"
echo "Time in seconds: ${T}"
