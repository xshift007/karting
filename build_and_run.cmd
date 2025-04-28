@echo off
SETLOCAL

echo ==== 1) Backend: build con Compose ====
docker-compose -f docker-compose-ha.yml build backend1 || exit /b 1

echo ==== 2) Frontend: build con Compose ====
docker-compose -f docker-compose-ha.yml build frontend1 || exit /b 1

echo ==== 3) Levantar servicios en modo HA ====
docker-compose -f docker-compose-ha.yml up -d || exit /b 1

echo.
echo ==== ¡Listo! ====
echo Frontend → http://localhost:8070
echo API      → http://localhost:8070/api
ENDLOCAL
