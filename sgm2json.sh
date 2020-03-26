#!/bin/bash

# pipe for concurrent
[ -e ./fd1 ] || mkfifo ./fd1
exec 3<>./fd1
rm -rf ./fd1

# token
for i in $(seq 1 10); do
  echo >&3
done

for sgmlFile in ./corpora/fbis/*.split; do
  read -u3 # get 1 token
  {
    osx --dtd_location=./corpora/fbis.dtd $sgmlFile >$sgmlFile.xml
    ./xml2json -t xml2json -o $sgmlFile.xml.json $sgmlFile.xml
    echo >&3 # token back
  } &
done

for sgmlFile in ./corpora/fr94/*.split; do
  read -u3 # get 1 token
  {
    osx --dtd_location=./corpora/fr94.dtd $sgmlFile >$sgmlFile.xml
    ./xml2json -t xml2json -o $sgmlFile.xml.json $sgmlFile.xml
    echo >&3 # token back
  } &
done

for sgmlFile in ./corpora/ft/*.split; do
  read -u3 # get 1 token
  {
    osx --dtd_location=./corpora/ft.dtd $sgmlFile >$sgmlFile.xml
    ./xml2json -t xml2json -o $sgmlFile.xml.json $sgmlFile.xml
    echo >&3 # token back
  } &
done

for sgmlFile in ./corpora/latimes/*.split; do
  read -u3 # get 1 token
  {
    osx --dtd_location=./corpora/latimes.dtd $sgmlFile >$sgmlFile.xml
    ./xml2json -t xml2json -o $sgmlFile.xml.json $sgmlFile.xml
    echo >&3 # token back
  } &
done

for sgmlFile in ./test/*.split; do
  read -u3 # get 1 token
  {
    osx --dtd_location=./corpora/fr94.dtd $sgmlFile >$sgmlFile.xml
    ./xml2json -t xml2json -o $sgmlFile.xml.json $sgmlFile.xml
    echo >&3 # token back
  } &
done

wait

# close fifo read/write
exec 3<&-
exec 3>&-
