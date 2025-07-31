@echo off
title Chain Reaction Cleanup
color 0C
echo ========================================
echo    CHAIN REACTION PORT CLEANUP
echo ========================================
echo.
echo This script will forcefully stop all Chain Reaction services
echo and free up the ports they were using.
echo.
echo Ports to clean: 8080 (Backend), 5173-5175 (Frontend)
echo.
pause

echo Cleaning up Chain Reaction services...
echo.

REM Stop Spring Boot backend (Java processes)
echo [1/4] Stopping backend Java processes...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO csv 2^>nul ^| findstr "chainReaction"') do (
    echo Stopping Java process PID %%i...
    taskkill /PID %%i /F >nul 2>&1
)

REM Clean up port 8080 (backend)
echo [2/4] Cleaning up backend port 8080...
for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":8080"') do (
    echo Stopping process using port 8080 (PID %%i)...
    taskkill /PID %%i /F >nul 2>&1
)

REM Stop Node.js frontend processes
echo [3/4] Stopping frontend Node.js processes...
taskkill /F /IM node.exe >nul 2>&1
echo Node.js processes stopped

REM Clean up frontend ports 5173-5175
echo [4/4] Cleaning up frontend ports 5173-5175...
for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":%%p"') do (
        echo Stopping process using port %%p (PID %%i)...
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo.
echo ========================================
echo    CLEANUP COMPLETED
echo ========================================
echo.
echo ✓ All Chain Reaction services stopped
echo ✓ Ports 8080, 5173-5175 freed
echo.
echo You can now restart the application safely.
echo.
echo Checking remaining port usage...
echo.

REM Show what's still using the ports (if anything)
echo Port 8080 status:
netstat -ano | findstr ":8080" || echo   No processes using port 8080

echo.
echo Ports 5173-5175 status:
for /L %%p in (5173,1,5175) do (
    echo Port %%p:
    netstat -ano | findstr ":%%p" || echo   No processes using port %%p
)

echo.
echo ========================================
echo.
echo Press any key to exit...
pause >nul
