#!/usr/bin/env bash

set -eux

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

SRV_BINARIES_DIR="/opt/binaries"
USER_SERVER=$DEPLOY_USER@$DEPLOY_SERVER

TM_VERSION=0.2

mkdir -p target/tmp-binaries

find target/ -name "*.deb" -exec mv {} target/tmp-binaries/trailer-maker-${TM_VERSION}.deb \;
find target/ -name "*.tgz" -exec mv {} target/tmp-binaries/trailer-maker-${TM_VERSION}.tgz \;
find target/ -name "*.rpm" -exec mv {} target/tmp-binaries/trailer-maker-${TM_VERSION}.rpm \;

rsync -av --progress --delete target/tmp-binaries/* $USER_SERVER:$SRV_BINARIES_DIR/

