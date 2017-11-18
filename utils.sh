#!/usr/bin/env bash
# mass renaming

find /media/alex/Sata2/Other/trailer-maker -name "* *" -type d | rename 's/ /_/g'    # do the directories first
find /media/alex/Sata2/Other/trailer-maker -name "* *" -type f | rename 's/ /_/g'

# mass executing trailer maker
find /media/alex/Sata2/Other/trailer-maker -iname "*.mp4" -exec java -jar trailer-maker.jar -f {} -d 15000 -l 1000 -s 3000 -o {} \;