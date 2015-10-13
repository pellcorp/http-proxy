#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java -cp "$DIR/lib/*" -Dlog4j.configuration=file:$DIR/log4j.properties com.pellcorp.proxy.cmd.ProxyServerCmd $@

