@echo off
echo =====================================================================
echo  Starting CricPulse Pro: Spring Boot Cricket Management Backend
echo =====================================================================
echo Java Version:
java -version
echo.
cd /d "%~dp0backend"
call mvnw.cmd spring-boot:run
pause
