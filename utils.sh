#!/usr/bin/env bash
# mass renaming

find /media/ -name "* *" -type d | rename 's/ /_/g'    # do the directories first
find /media/ -name "* *" -type f | rename 's/ /_/g'

# mass executing trailer maker
find /media/ -iname "*.mp4" -exec java -jar trailer-maker.jar -f {} -d 15000 -l 1000 -s 4000 -o /media/trailers/ --prepend-length --preserve \;
find /media/ -iname "*.mp4" -exec java -jar trailer-maker.jar -f {} -d 30000 -l 1500 -s 2000 -o /media/trailers/ --prepend-length --preserve \;

