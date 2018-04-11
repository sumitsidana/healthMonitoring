#!/bin/bash

SAVEIFS=$IFS
IFS=$(echo -en "\n\b")

DIR="train/"
paths="$(find "$DIR" -type f)"
count=0
for path in $paths
do
	count=$((count+1));
        f="${path##*/}"
	dir="$(dirname "$path")"
	echo "path: " "$path"
        echo "file: " "$f"
	echo "directory: " "$dir"
	T="$(date +%s)"
	javac -cp createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-corenlp-3.2.0-models.jar:.:  createATAMInput/CreateATAMInputStopWordsUnleashedWithLowerCaseIssue.java createATAMInput/StanfordLemmatizer.java
	java -cp ./:./createATAMInput/:createATAMInput/stanford-corenlp-3.3.1.jar:createATAMInput/stanford-corenlp-3.5.0.jar:createATAMInput/stanford-postagger-full-2014-01-04/:createATAMInput/stanford-corenlp-3.2.0-models.jar createATAMInput.CreateATAMInputStopWordsUnleashedWithLowerCaseIssue ../../data/stopwords.txt ../../data/DataSets/ "$path" "$f" "$dir"
	javac -cp commons-math4-4.0-SNAPSHOT.jar *.java
	java -cp commons-math4-4.0-SNAPSHOT.jar:. LearnTopicModel -model atam -Z 25 -A 25 -iters 5000 -input "$dir/InputToATAMUsingLemmatizer$f"
done
echo "number of paths: " "$count"
IFS=$SAVEIFS
