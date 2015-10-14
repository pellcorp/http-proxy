@echo off
setlocal ENABLEDELAYEDEXPANSION

set EXE_DIR=%~dp0
FOR /R %EXE_DIR%\lib %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G

set ARG1=%1
set ARG2=%2
set ARG3=%3
set ARG4=%4
set ARG5=%5
set ARG6=%6
set ARG7=%7
set ARG8=%8
set ARG9=%9
shift
set ARG10=%9
shift
set ARG11=%9
shift
set ARG12=%9
shift
set ARG13=%9
shift
set ARG14=%9
shift
set ARG15=%9
shift
set ARG16=%9
shift
set ARG17=%9
shift
set ARG18=%9
shift
set ARG19=%9
shift
set ARG20=%9

java -Dlog4j.configuration=file:%EXE_DIR%\log4j.properties com.pellcorp.proxy.cmd.ProxyServerCmd %ARG1% %ARG2% %ARG3% %ARG4% %ARG5% %ARG6% %ARG7% %ARG8% %ARG9% %ARG10% %ARG11% %ARG12% %ARG13% %ARG14% %ARG15% %ARG16% %ARG17% %ARG18% %ARG19% %ARG20% 

