#!/bin/bash
# This file iterates over the Sub-directories containt Java Projects,
# while looking up for dot-files and converting them to corresponding
# ps-files.

for dir in ./*/
do
	dir=${dir%*/}
	# echo ${dir##*/}
	for f in $dir/dot/*.dot
	do
		fname=$(basename $f .dot)
		dot -Tps $f -o $dir/ps/$fname.ps
	done
done

## The  code below can used to iterate and remove files in some
## particular directory.
# for dir in ./*/
# do
# 	dir=${dir%*/}
# 	rm $dir/ps/*.ps
# done
