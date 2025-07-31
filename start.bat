@echo off
title Chain Reaction Launcher
color 0A
setlocal enabledelayedexpansion

echo ========================================
echo    CHAIN REACTION GAME LAUNCHER
echo ========================================
echo.
echo Starting application components...
echo.

REM Backend
echo [1/2] Starting Spring Boot Backend...
start "Chain Reaction Backend" cmd /k "start-backend.bat"

REM Wait for backend to initialize
echo Waiting for backend to initialize...
timeout /t 8 /nobreak >nul

REM Frontend
echo [2/2] Starting React Frontend...
start "Chain Reaction Frontend" cmd /k "start-frontend.bat"

REM Wait a moment for frontend to start
timeout /t 3 /nobreak >nul

echo.
echo ========================================
echo    APPLICATION STARTED SUCCESSFULLY
echo ========================================
echo.
echo Backend API:  http://localhost:8080
echo Frontend UI:  http://localhost:5174
echo.
echo Both services are running in separate windows.
echo.
echo ========================================
echo    CLEANUP OPTIONS
echo ========================================
echo.
echo Press 'Q' to quit and cleanup all services
echo Press 'C' to cleanup ports manually
echo Press any other key to exit launcher only
echo.
choice /c QCX /n /m "Your choice: "

if !errorlevel!==1 goto :cleanup
if !errorlevel!==2 goto :manual_cleanup
goto :exit_only

:cleanup
echo.
echo Cleaning up services...
echo.

REM Kill Spring Boot processes (Java processes running our jar)
echo Stopping backend services...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO csv ^| findstr "chainReaction"') do (
    taskkill /PID %%i /F >nul 2>&1
)

REM Kill processes using port 8080
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080"') do (
    taskkill /PID %%i /F >nul 2>&1
)

REM Kill Node.js processes (React dev server)
echo Stopping frontend services...
taskkill /F /IM node.exe >nul 2>&1

REM Kill processes using port 5173-5175 (common Vite ports)
for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":%%p"') do (
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo ✓ Services stopped and ports freed
timeout /t 2 /nobreak >nul
goto :end

:manual_cleanup
echo.
echo Manual cleanup commands:
echo.
echo To stop backend (port 8080):
echo   for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080"') do taskkill /PID %%i /F
echo.
echo To stop frontend (Node.js):
echo   taskkill /F /IM node.exe
echo.
echo To check what's using ports:
echo   netstat -ano ^| findstr ":8080"
echo   netstat -ano ^| findstr ":5173"
echo.
pause
goto :end

:exit_only
echo.
echo Launcher exiting - services will continue running.
echo Use the cleanup option or close the service windows manually.
echo.

:end
echo.
echo ========================================
echo.
echo Press any key to close this launcher...
pause >nul
