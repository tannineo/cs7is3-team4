#!/bin/bash

mvn clean

mvn package

cp ./target/cs7is3-group4-1.0.0-SNAPSHOT.jar ./InfoSeekers.jar
