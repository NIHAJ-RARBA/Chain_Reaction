@echo off
title Chain Reaction Backend
color 0B
cd /d "e:\CODE\Java\AI\Adversarial Search\Chain_Reaction\backend\chainReaction"

REM Set up cleanup on exit
set "CLEANUP_NEEDED=true"

echo ========================================
echo    CHAIN REACTION BACKEND SERVER
echo ========================================
echo.

REM Try to find an existing jar file first
echo [1/3] Checking for existing application...
if exist "target\chainReaction-0.0.1-SNAPSHOT.jar" (
    echo ✓ Found existing jar file
    echo [2/3] Starting Spring Boot application...
    echo.
    echo Press Ctrl+C to stop the server properly
    echo.
    java -jar "target\chainReaction-0.0.1-SNAPSHOT.jar"
    goto :cleanup
)

echo [2/3] No jar file found, building application...
echo.

REM Get the 8.3 short path format to avoid spaces
for %%A in ("%USERPROFILE%") do set "SHORT_HOME=%%~sA"
echo Using user home: %SHORT_HOME%

REM Set environment variables using short path
set "HOME=%SHORT_HOME%"
set "MAVEN_OPTS=-Dfile.encoding=UTF-8"
set "MAVEN_USER_HOME=%SHORT_HOME%\.m2"

echo.
echo Building with Maven...
mvnw.cmd clean package -DskipTests

REM Check if build created a jar
echo.
echo [3/3] Starting built application...
if exist "target\chainReaction-0.0.1-SNAPSHOT.jar" (
    echo ✓ Build successful!
    echo Starting Spring Boot application...
    echo.
    echo Press Ctrl+C to stop the server properly
    echo.
    java -jar "target\chainReaction-0.0.1-SNAPSHOT.jar"
    goto :cleanup
)

REM Fallback: look for any jar file
for %%f in (target\*.jar) do (
    if not "%%~nxf"=="target\*.jar" (
        if not "%%~nxf"==chainReaction-0.0.1-SNAPSHOT.jar.original (
            echo Found jar file: %%f
            echo Press Ctrl+C to stop the server properly
            echo.
            java -jar "%%f"
            goto :cleanup
        )
    )
)

echo ✗ Failed to build or run the application
echo Check the Maven build output above for errors.
goto :end

:cleanup
echo.
echo ========================================
echo Backend server stopped.
echo Cleaning up port 8080...

REM Clean up any remaining processes on port 8080
for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":8080"') do (
    echo Stopping PID %%i...
    taskkill /PID %%i /F >nul 2>&1
)

echo ✓ Port 8080 cleaned up
echo ========================================

:end
echo.
echo Press any key to close this window...
pause >nul
