#!/bin/sh

PRG=$0

PRGDIR=`dirname $PRG`

export ESRA_HOME=`cd "$PRGDIR/.." > /dev/null; pwd`

exec java -cp $ESRA_HOME/lib/commons-cli-1.2.jar:$ESRA_HOME/lib/esra-poc-1.0-SNAPSHOT.jar com.liferay.tools.esra.shell.EsraShell $@