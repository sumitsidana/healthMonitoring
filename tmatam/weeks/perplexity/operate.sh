#!/bin/bash

SAVEIFS=$IFS
IFS=$(echo -en "\n\b")

DIR="../data/perplexity/train/"
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
	echo "directoryToBeMade:" "$directoryTobeMade"	
	mkdir -p "../data/perplexity/topicailmentcopyfiles/$directoryTobeMade/"
	l=${#f}
	 if [ "$l" -eq 67 ] || [ "$l" -eq 68 ] ;
	#if [ "$l" -eq 41 ] ;
	then
		echo "$f"
		cp "$path" "../data/perplexity/topicailmentcopyfiles/$directoryTobeMade/"
	fi
done
echo "number of paths: " "$count"
IFS=$SAVEIFS
