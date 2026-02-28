@echo off
echo ===== Compiling RevPay =====
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b 1
)
echo.
echo ===== Compilation Successful =====
echo.
