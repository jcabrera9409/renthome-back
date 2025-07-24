#!/bin/bash
# ========================================
# SETUP DE PRODUCCIÓN - JWT KEYS SEGURAS
# ========================================

echo "🔐 Configurando JWT Keys para Producción..."

# Crear directorio seguro para keys
sudo mkdir -p /etc/renthome/keys
sudo chmod 700 /etc/renthome/keys

# Generar par de keys RSA
echo "📝 Generando par de keys RSA..."
openssl genrsa -out /tmp/privateKey.pem 2048
openssl rsa -pubout -in /tmp/privateKey.pem -out /tmp/publicKey.pem

# Mover keys al directorio seguro
sudo mv /tmp/privateKey.pem /etc/renthome/keys/
sudo mv /tmp/publicKey.pem /etc/renthome/keys/

# Establecer permisos restrictivos
sudo chmod 600 /etc/renthome/keys/privateKey.pem  # Solo lectura para root
sudo chmod 644 /etc/renthome/keys/publicKey.pem   # Lectura para todos

# Establecer ownership
sudo chown root:root /etc/renthome/keys/*

echo "✅ Keys generadas y configuradas:"
echo "   Private Key: /etc/renthome/keys/privateKey.pem"
echo "   Public Key:  /etc/renthome/keys/publicKey.pem"

echo ""
echo "🌍 Variables de entorno requeridas:"
echo "export JWT_PUBLIC_KEY_PATH=file:///etc/renthome/keys/publicKey.pem"
echo "export JWT_PRIVATE_KEY_PATH=file:///etc/renthome/keys/privateKey.pem"

echo ""
echo "🐳 Para Docker:"
echo "docker run -e JWT_PUBLIC_KEY_PATH=file:///etc/renthome/keys/publicKey.pem \\"
echo "           -e JWT_PRIVATE_KEY_PATH=file:///etc/renthome/keys/privateKey.pem \\"
echo "           -v /etc/renthome/keys:/etc/renthome/keys:ro \\"
echo "           renthome-app:latest"

echo ""
echo "☁️ Para Kubernetes:"
echo "kubectl create secret generic jwt-keys \\"
echo "  --from-file=publicKey.pem=/etc/renthome/keys/publicKey.pem \\"
echo "  --from-file=privateKey.pem=/etc/renthome/keys/privateKey.pem"
