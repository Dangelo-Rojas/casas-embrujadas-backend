@echo off
chcp 65001 >nul
REM Levanta el backend y guarda todo lo que muestra en backend.log.
REM Si ya habia uno corriendo en el puerto 8080, lo cierra primero (para cargar los cambios nuevos)
cd /d "%~dp0"
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do taskkill /F /PID %%p >nul 2>&1
powershell -NoProfile -Command "& '.\mvnw.cmd' spring-boot:run 2>&1 | Tee-Object -FilePath backend.log"
