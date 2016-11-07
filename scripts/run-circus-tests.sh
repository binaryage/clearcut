#!/usr/bin/env bash

set -e

pushd `dirname "${BASH_SOURCE[0]}"` > /dev/null
source "./config.sh"

pushd "$ROOT"

export TABELLION_ELIDE_DEVTOOLS=1

lein with-profile +circus run -m "tabellion.circus"

popd
