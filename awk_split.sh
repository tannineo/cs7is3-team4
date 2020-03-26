#!/bin/bash

# pipe for concurrent
[ -e ./fd1 ] || mkfifo ./fd1
exec 3<>./fd1
rm -rf ./fd1

# token
for i in $(seq 1 10); do
  echo >&3
done

for sgmlFile in ./corpora/*/*.sgm; do
  read -u3 # get 1 token
  {
    # remove comments
    gsed '/^<!--/d' $sgmlFile >$sgmlFile"nc"
    # remove the empty lines
    gsed '/^$/d' $sgmlFile"nc" >$sgmlFile"ne"
    # split file using <DOC>
    awk 'BEGIN {NUM=0;}; /<DOC>/ {NUM++; filename=FILENAME"_"NUM".split"}; {print >filename}' $sgmlFile"ne"

    echo >&3 # token back
  } &
done

wait

# close fifo read/write
exec 3<&-
exec 3>&-

for sgmlFile in ./test/*.sgm; do
  {
    # remove comments
    gsed '/^<!--/d' $sgmlFile >$sgmlFile"nc"
    # remove the empty lines
    gsed '/^$/d' $sgmlFile"nc" >$sgmlFile"ne"
    # split file using <DOC>
    awk 'BEGIN {NUM=0;}; /<DOC>/ {NUM++; filename=FILENAME"_"NUM".split"}; {print >filename}' $sgmlFile"ne"
  } &
done
