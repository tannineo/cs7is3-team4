#!/bin/bash

find ./test -name "*.sgmn*" -print0 | xargs -0 rm
# find ./test -name "*.split" -print0 | xargs -0 rm
# find ./test -name "*.xml" -print0 | xargs -0 rm
# find ./test -name "*.json" -print0 | xargs -0 rm
find ./corpora -name "*.sgmn*" -print0 | xargs -0 rm
# find ./corpora -name "*.split" -print0 | xargs -0 rm
# find ./corpora -name "*.xml" -print0 | xargs -0 rm
# find ./corpora -name "*.json" -print0 | xargs -0 rm
