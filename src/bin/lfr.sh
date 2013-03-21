#!/bin/sh

PRG=$0

PRGDIR=`dirname $PRG`

export ESRA_HOME=`cd "$PRGDIR/.." > /dev/null; pwd`

exec java -cp "$ESRA_HOME/lib/*" com.liferay.tools.esra.shell.EsraShell $@