#!/usr/bin/env bash

pushd () {
    command pushd "$@" > /dev/null
}

popd () {
    command popd "$@" > /dev/null
}

pushd `dirname "${BASH_SOURCE[0]}"`

cd ..

ROOT=`pwd`
PROJECT_VERSION_FILE="src/lib/clearcut/version.clj"
PROJECT_FILE="project.clj"
DEV_FIXTURES_SERVER_ROOT="$ROOT/test/resources"
DEV_FIXTURES_SERVER_PORT=7119
CLEARCUT_BROWSER_TESTS_USER_PROFILE=".profiles/clearcut-browser-tests"

popd
