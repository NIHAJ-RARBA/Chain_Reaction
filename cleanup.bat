@echo off
REM This script monitors the main launcher and cleans up when it exits

set "MAIN_PID=%1"
if "%MAIN_PID%"=="" (
    echo Error: No process ID provided
    exit /b 1
)

echo Monitoring process %MAIN_PID% for cleanup...

:monitor_loop
REM Check if the main process is still running
tasklist /FI "PID eq %MAIN_PID%" 2>nul | find "%MAIN_PID%" >nul
if errorlevel 1 goto :do_cleanup

REM Wait 2 seconds before checking again
timeout /t 2 /nobreak >nul 2>nul
goto :monitor_loop

:do_cleanup
echo.
echo Main launcher closed - cleaning up services...

REM First try to gracefully close windows by window title
echo Closing backend and frontend windows...
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

REM Kill all cmd processes except this cleanup script
echo Stopping all remaining cmd windows...
for /f "skip=1 tokens=2" %%i in ('tasklist /FI "IMAGENAME eq cmd.exe" /FO csv /NH 2^>nul') do (
    set "pid=%%~i"
    if "!pid!" neq "%PID%" (
        taskkill /PID !pid! /F >nul 2>&1
    )
)

REM Kill all Java processes (Spring Boot)
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1

REM Kill all Node.js processes (React dev server)
echo Stopping all Node.js processes...
taskkill /F /IM node.exe >nul 2>&1

REM Force kill anything on our ports
echo Freeing ports 8080 and 5173-5175...
for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":8080"') do (
    taskkill /PID %%i /F >nul 2>&1
)

for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":%%p"') do (
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo ✓ All services stopped, windows closed, and ports freed
timeout /t 3 /nobreak >nul
