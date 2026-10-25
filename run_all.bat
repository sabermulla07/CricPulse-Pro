@echo off
echo =====================================================================
echo  Starting CricPulse Pro: Full System Launch
echo  Spring Boot Backend + React Frontend + MySQL
echo =====================================================================

start "CricPulse Backend (Spring Boot)" cmd /c "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run"
echo Backend starting on http://localhost:8080 ...
timeout /t 6 /nobreak >nul

start "CricPulse Frontend (Vite React)" cmd /c "cd /d "%~dp0frontend" && npm run dev"
echo Frontend starting on http://localhost:5173 ...
timeout /t 3 /nobreak >nul

start http://localhost:8080/
echo System running! Access dashboard at:
echo 1) Embedded Dashboard: http://localhost:8080/
echo 2) React Dev Server:   http://localhost:5173/
echo.
pause
