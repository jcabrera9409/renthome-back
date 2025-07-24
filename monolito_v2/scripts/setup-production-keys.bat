@echo off
REM ========================================
REM SETUP DE PRODUCCIÓN - JWT KEYS SEGURAS (WINDOWS)
REM ========================================

echo 🔐 Configurando JWT Keys para Producción (Windows)...

REM Crear directorio para keys
if not exist "C:\renthome\keys" mkdir "C:\renthome\keys"

echo 📝 Para generar keys RSA en Windows, usar OpenSSL o PowerShell:
echo.
echo OpenSSL (si está instalado):
echo openssl genrsa -out C:\renthome\keys\privateKey.pem 2048
echo openssl rsa -pubout -in C:\renthome\keys\privateKey.pem -out C:\renthome\keys\publicKey.pem
echo.
echo PowerShell (alternativo):
echo $cert = New-SelfSignedCertificate -Subject "CN=RentHome JWT" -CertStoreLocation "Cert:\CurrentUser\My" -KeyAlgorithm RSA -KeyLength 2048
echo.
echo ✅ Ubicación recomendada para keys:
echo    Private Key: C:\renthome\keys\privateKey.pem
echo    Public Key:  C:\renthome\keys\publicKey.pem
echo.
echo 🌍 Variables de entorno requeridas:
echo set JWT_PUBLIC_KEY_PATH=file:///C:/renthome/keys/publicKey.pem
echo set JWT_PRIVATE_KEY_PATH=file:///C:/renthome/keys/privateKey.pem
echo.
echo O en PowerShell:
echo $env:JWT_PUBLIC_KEY_PATH="file:///C:/renthome/keys/publicKey.pem"
echo $env:JWT_PRIVATE_KEY_PATH="file:///C:/renthome/keys/privateKey.pem"
echo.
echo 🐳 Para Docker en Windows:
echo docker run -e JWT_PUBLIC_KEY_PATH=file:///C:/renthome/keys/publicKey.pem ^
echo            -e JWT_PRIVATE_KEY_PATH=file:///C:/renthome/keys/privateKey.pem ^
echo            -v C:\renthome\keys:C:\renthome\keys:ro ^
echo            renthome-app:latest

pause
