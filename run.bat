@echo off
title SkillBridge Desktop Application
cd /d "%~dp0"

echo ========================================================
echo          Starting SkillBridge Desktop Application
echo ========================================================
echo.

set JAVA_BIN=java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    set JAVA_BIN="C:\Program Files\Java\jdk-26.0.2\bin\java.exe"
)

set CP=target\classes;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar;%USERPROFILE%\.m2\repository\com\formdev\flatlaf\3.4.1\flatlaf-3.4.1.jar

echo Launching SkillBridge window...
%JAVA_BIN% -cp "%CP%" com.skillbridge.gui.MainFrame

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with code %ERRORLEVEL%.
    pause
)
