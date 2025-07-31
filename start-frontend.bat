@echo off
title Chain Reaction Frontend
color 0E
cd /d "e:\CODE\Java\AI\Adversarial Search\Chain_Reaction\frontend\chain-reaction-frontend"

echo ========================================
echo    CHAIN REACTION FRONTEND SERVER
echo ========================================
echo.
echo [1/2] Checking dependencies...

REM Check if node_modules exists
if exist "node_modules\" (
    echo ✓ Dependencies found
) else (
    echo Installing dependencies...
    npm install
    if %ERRORLEVEL% neq 0 (
        echo ✗ Failed to install dependencies
        goto :error
    )
    echo ✓ Dependencies installed
)

echo.
echo [2/2] Starting React development server...
echo.
echo Frontend will be available at: http://localhost:5173
echo (If port 5173 is busy, Vite will use the next available port)
echo.
echo Press Ctrl+C to stop the server properly
echo.

REM Start the development server and open browser
echo Opening browser...
start http://localhost:5173
npm run dev

goto :cleanup

:error
echo.
echo ========================================
echo Frontend startup failed.
echo Please check the error messages above.
echo ========================================
goto :end

:cleanup
echo.
echo ========================================
echo Frontend server stopped.
echo Cleaning up Node.js processes and ports...

REM Clean up Node.js processes
taskkill /F /IM node.exe >nul 2>&1

REM Clean up common Vite dev server ports
for /L %%p in (5173,1,5175) do (
    for /f "tokens=5" %%i in ('netstat -ano 2^>nul ^| findstr ":%%p"') do (
        echo Stopping process using port %%p (PID %%i)...
        taskkill /PID %%i /F >nul 2>&1
    )
)

echo ✓ Frontend ports cleaned up
echo ========================================

:end
echo.
echo Press any key to close this window...
pause >nul
