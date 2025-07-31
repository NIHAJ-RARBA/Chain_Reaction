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

REM STEP 3: Start Frontend
echo [STEP 3/3] Starting React Frontend...
start "Chain Reaction Frontend" cmd /k "start-frontend.bat"

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
echo Close this window to stop all services and cleanup ports.
echo.

:keep_running
timeout /t 10 /nobreak >nul 2>nul || goto :cleanup
goto :keep_running

REM This section runs when the window is closed (Ctrl+C or X button)
:cleanup
echo.
echo Cleaning up services...

REM Kill the backend and frontend windows by title
taskkill /FI "WINDOWTITLE eq Chain Reaction Backend*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Chain Reaction Frontend*" /F >nul 2>&1

REM Kill Spring Boot processes (Java processes running our jar)
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO csv ^| findstr "chainReaction"') do (
    taskkill /PID %%i /F >nul 2>&1
)

REM Kill processes using port 8080
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080"') do (
    taskkill /PID %%i /F >nul 2>&1
)

REM Kill Node.js processes (React dev server)
taskkill /F /IM node.exe >nul 2>&1

REM Kill processes using port 5173-5175 (common Vite ports)
for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":%%p"') do (
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo ✓ Services stopped and ports freed
