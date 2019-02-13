#!/bin/bash
T="$(date +%s)"
# All the files required are present here: https://1drv.ms/f/s!Alr4JS0ifh7GgfMgSWaIhBI92y0c5Q
javac -cp createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-corenlp-3.2.0-models.jar:.:  createATAMInput/CreateATAMInputStopWordsUnleashedWithLowerCaseIssue.java createATAMInput/StanfordLemmatizer.java
java -cp ./:./createATAMInput/:createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-postagger-full-2014-01-04/:createATAMInput/stanford-corenlp-3.2.0-models.jar createATAMInput.CreateATAMInputStopWordsUnleashedWithLowerCaseIssue stopwords.txt DataSets/ HealthFinalVersion
#
javac -cp commons-math-2.1.jar *.java
java -cp commons-math-2.1.jar:. LearnTopicModel -model atam -Z 25 -A 25 -iters 5000 -input InputToATAMUsingLemmatizer

echo 'Extracting just the symptoms from ATAM output'
python topwords_atam.py InputToATAMUsingLemmatizer.assign > output_atam.txt

T="$(($(date +%s)-T))"
echo "Time in seconds: ${T}"
