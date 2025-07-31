@echo off
title Chain Reaction Launcher
color 0A
setlocal enabledelayedexpansion

echo ========================================
echo    CHAIN REACTION GAME LAUNCHER
echo ========================================
echo.

REM Check if JAR exists
set "JAR_PATH=backend\chainReaction\target\chainReaction-0.0.1-SNAPSHOT.jar"
if exist "%JAR_PATH%" (
    echo ✓ JAR file found - skipping build
    goto :start_backend
)

REM STEP 1: Build if needed
echo [STEP 1/3] Building application...
echo JAR file not found, building with Maven...
cd /d "backend\chainReaction"

REM Get the 8.3 short path format to avoid spaces
for %%A in ("%USERPROFILE%") do set "SHORT_HOME=%%~sA"
echo Using user home: %SHORT_HOME%

REM Set environment variables using short path
set "HOME=%SHORT_HOME%"
set "MAVEN_OPTS=-Dfile.encoding=UTF-8"
set "MAVEN_USER_HOME=%SHORT_HOME%\.m2"

echo.
echo Building with Maven...
call mvnw.cmd clean package -DskipTests
cd /d "..\..\"

if not exist "%JAR_PATH%" (
    echo ✗ Build failed! JAR file not created.
    pause
    exit /b 1
)
echo ✓ Build completed successfully!
echo.

:start_backend
REM STEP 2: Start Backend
echo [STEP 2/3] Starting Spring Boot Backend...
start "Chain Reaction Backend" cmd /k "start-backend.bat"

REM Wait for backend to start properly
echo Waiting for backend to start...
timeout /t 12 /nobreak >nul

REM Minimize the backend window after it's started
echo Minimizing backend window...
powershell -Command "Add-Type -AssemblyName Microsoft.VisualBasic; [Microsoft.VisualBasic.Interaction]::AppActivate('Chain Reaction Backend'); Start-Sleep 1; Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.SendKeys]::SendWait('%{F9}')" >nul 2>&1

REM STEP 3: Start Frontend
echo [STEP 3/3] Starting React Frontend...
start "Chain Reaction Frontend" cmd /k "start-frontend.bat"

REM Wait for frontend to start
timeout /t 5 /nobreak >nul

REM Minimize the frontend window after it's started
echo Minimizing frontend window...
powershell -Command "Add-Type -AssemblyName Microsoft.VisualBasic; [Microsoft.VisualBasic.Interaction]::AppActivate('Chain Reaction Frontend'); Start-Sleep 1; Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.SendKeys]::SendWait('%{F9}')" >nul 2>&1

REM Wait for frontend to start
timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo    APPLICATION STARTED SUCCESSFULLY
echo ========================================
echo.
echo Backend API:  http://localhost:8080
echo Frontend UI:  http://localhost:5174
echo.
echo Both services are running in separate windows.
@REM echo Close this window to stop all services and cleanup ports.
echo.

REM Start cleanup monitor in background
start /min "" cmd /c "cleanup.bat %PID%"

echo Press any key to stop all services or close this window...
pause >nul

REM Manual cleanup if user pressed a key
echo.
echo Stopping services...

REM First try to close windows by title
taskkill /FI "WINDOWTITLE eq Chain Reaction Backend*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Chain Reaction Frontend*" /F >nul 2>&1

REM Also try to kill any window containing "vite", "react", or "npm"
taskkill /FI "WINDOWTITLE eq *vite*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq *react*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq *npm*" /F >nul 2>&1

REM Kill any cmd window running from our frontend directory
taskkill /FI "WINDOWTITLE eq *chain-reaction-frontend*" /F >nul 2>&1

REM Use PowerShell to find and kill cmd processes running our batch files
echo Finding processes by command line...
powershell -Command "Get-WmiObject Win32_Process | Where-Object {$_.CommandLine -like '*start-frontend.bat*' -or $_.CommandLine -like '*start-backend.bat*'} | ForEach-Object {Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue}"

REM Kill all cmd processes except this one
for /f "skip=1 tokens=2" %%i in ('tasklist /FI "IMAGENAME eq cmd.exe" /FO csv /NH 2^>nul') do (
    set "pid=%%~i"
    if "!pid!" neq "%PID%" (
        taskkill /PID !pid! /F >nul 2>&1
    )
)

REM Kill all Java and Node processes
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1

REM Clean up ports
for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":8080"') do (
    taskkill /PID %%i /F >nul 2>&1
)
for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":%%p"') do (
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo ✓ All services stopped and windows closed
