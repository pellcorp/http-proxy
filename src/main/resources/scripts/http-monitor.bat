@echo off
setlocal ENABLEDELAYEDEXPANSION

set EXE_DIR=%~dp0
FOR /R %EXE_DIR%\lib %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G

java -Dlog4j.configuration=file:%EXE_DIR%\log4j.properties com.pellcorp.proxy.cmd.ProxyServerCmd %1 %2 %3 $4
