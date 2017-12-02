#!/usr/bin/env bash

set -ux

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

sbt scalafmt scalastyle \
    compile test:compile \
    test:test             && echo "### Unit Tests: OK"
