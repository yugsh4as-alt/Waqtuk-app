@echo off
setlocal

set DIRNAME=%~dp0
set JAVA_EXE=java.exe

rem Check Java
%JAVA_EXE% -version >NUL 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Make sure JAVA_HOME is set or java is in PATH.
    pause
    exit /b 1
)

rem Download gradle-wrapper.jar if missing
if not exist "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading gradle-wrapper.jar...
    powershell -Command ^
      "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; " ^
      "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar' " ^
      "-OutFile '%DIRNAME%gradle\wrapper\gradle-wrapper.jar'"
    if errorlevel 1 (
        echo FAILED to download gradle-wrapper.jar
        echo Download manually from: https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar
        echo Place it in: gradle\wrapper\gradle-wrapper.jar
        pause
        exit /b 1
    )
    echo Downloaded successfully.
)

%JAVA_EXE% -jar "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" %*
