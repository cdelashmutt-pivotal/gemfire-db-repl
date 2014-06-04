@echo off
setlocal

REM ********** 
REM Launcher for the gemfirerepl server
REM ********** 

if "%JAVA_LIBRARY_PATH%" == "" goto launch
set LAUNCH_ARGS="-Djava.library.path=%JAVA_LIBRARY_PATH%"

:launch
java %LAUNCH_ARGS% -jar %~dp0gemfirerepl.jar
