#!/usr/bin/env bash

set -eux

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

sudo apt-get install -y rpm
sbt debian:packageBin rpm:packageBin universal:packageZipTarball
