@echo off
echo ========================================
echo RevPay Database Setup Script
echo ========================================
echo.

REM Check if MySQL is accessible
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL not found in PATH
    echo Please install MySQL or add it to your PATH
    pause
    exit /b 1
)

echo Step 1: Creating Database...
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS revpay_db;"

echo Step 2: Importing Schema...
mysql -u root -p revpay_db < db_schema.sql

echo.
echo ========================================
echo Database setup complete!
echo ========================================
echo.
echo Next steps:
echo 1. Update DBConnection.java with your MySQL password
echo 2. Run: mvn clean install
echo 3. Run: mvn exec:java
echo.
pause
