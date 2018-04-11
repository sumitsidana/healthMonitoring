#!/bin/bash

SAVEIFS=$IFS
IFS=$(echo -en "\n\b")

DIR="data/perplexity/validinferences/"
paths="$(find "$DIR" -type f)"
count=0
for path in $paths
do
        count=$((count+1));
        f="${path##*/}"
        dir="$(dirname "$path")"
	directoryTobeMade="${dir##*/}"
       # echo "path: " "$path"
       # echo "file: " "$f"
       # echo "directory: " "$dir" 
	l=${#f}
	 if [ "$l" -eq 35 ] || [ "$l" -eq 34 ] ;
	#if [ "$l" -eq 41 ] ;
	then
		echo "directoryToBeMade:" "$directoryTobeMade"	
		mkdir -p "data/perplexity/wordcounts/$directoryTobeMade/"
		echo "$f"
		#cp "$path" "copyfiles/$directoryTobeMade/"
		python CalculationDistributionsInferences.py "$path" >"data/perplexity/wordcounts/$directoryTobeMade/"$f"_output_atam.txt"
	fi
done
echo "number of paths: " "$count"
IFS=$SAVEIFS
