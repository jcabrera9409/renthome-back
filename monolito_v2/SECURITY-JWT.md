# 🔐 JWT Security - Mejores Prácticas

## ⚠️ ADVERTENCIA DE SEGURIDAD

**NUNCA subas archivos `.pem` al repositorio Git**. Las claves privadas comprometidas pueden poner en riesgo toda la seguridad de la aplicación.

---

## 🎯 Configuración por Ambiente

### 🔧 DESARROLLO
```bash
# Crear directorio para keys de desarrollo
mkdir -p ~/renthome-keys/dev
cd ~/renthome-keys/dev

# Generar keys para desarrollo
openssl genrsa -out privateKey.pem 2048
openssl rsa -pubout -in privateKey.pem -out publicKey.pem

# Establecer variables de entorno
export JWT_PUBLIC_KEY_PATH="file:///$(pwd)/publicKey.pem"
export JWT_PRIVATE_KEY_PATH="file:///$(pwd)/privateKey.pem"
```

### 🚀 PRODUCCIÓN
```bash
# Ejecutar script de setup
./scripts/setup-production-keys.sh

# Variables de entorno en producción
export JWT_PUBLIC_KEY_PATH="file:///etc/renthome/keys/publicKey.pem"
export JWT_PRIVATE_KEY_PATH="file:///etc/renthome/keys/privateKey.pem"
```

### 🧪 TESTING
```bash
# Keys temporales para tests
mkdir -p /tmp/test-keys
openssl genrsa -out /tmp/test-keys/privateKey.pem 2048
openssl rsa -pubout -in /tmp/test-keys/privateKey.pem -out /tmp/test-keys/publicKey.pem
```

---

## 🐳 Docker Deployment

### Dockerfile Seguro
```dockerfile
FROM quarkus/ubi-quarkus-mandrel:23.0-java21

# NO copiar keys al container
# COPY keys/ /app/keys/  # ❌ NUNCA HAGAS ESTO

# Keys se montan como volúmenes externos
VOLUME /etc/renthome/keys

ENV JWT_PUBLIC_KEY_PATH="file:///etc/renthome/keys/publicKey.pem"
ENV JWT_PRIVATE_KEY_PATH="file:///etc/renthome/keys/privateKey.pem"

COPY target/quarkus-app/ /deployments/
```

### Docker Run
```bash
docker run -d \
  -e JWT_PUBLIC_KEY_PATH="file:///etc/renthome/keys/publicKey.pem" \
  -e JWT_PRIVATE_KEY_PATH="file:///etc/renthome/keys/privateKey.pem" \
  -v /etc/renthome/keys:/etc/renthome/keys:ro \
  renthome:latest
```

---

## ☁️ Kubernetes Deployment

### 1. Crear Secret
```bash
kubectl create secret generic jwt-keys \
  --from-file=publicKey.pem=/etc/renthome/keys/publicKey.pem \
  --from-file=privateKey.pem=/etc/renthome/keys/privateKey.pem
```

### 2. Deployment YAML
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: renthome-app
spec:
  template:
    spec:
      containers:
      - name: renthome
        image: renthome:latest
        env:
        - name: JWT_PUBLIC_KEY_PATH
          value: "file:///etc/jwt-keys/publicKey.pem"
        - name: JWT_PRIVATE_KEY_PATH
          value: "file:///etc/jwt-keys/privateKey.pem"
        volumeMounts:
        - name: jwt-keys
          mountPath: /etc/jwt-keys
          readOnly: true
      volumes:
      - name: jwt-keys
        secret:
          secretName: jwt-keys
```

---

## 🔄 Rotación de Keys

### Generar Nuevas Keys
```bash
# Backup de keys actuales
cp /etc/renthome/keys/privateKey.pem /etc/renthome/keys/privateKey.pem.backup
cp /etc/renthome/keys/publicKey.pem /etc/renthome/keys/publicKey.pem.backup

# Generar nuevas keys
openssl genrsa -out /etc/renthome/keys/privateKey.pem.new 2048
openssl rsa -pubout -in /etc/renthome/keys/privateKey.pem.new -out /etc/renthome/keys/publicKey.pem.new

# Reemplazar (requiere restart de aplicación)
mv /etc/renthome/keys/privateKey.pem.new /etc/renthome/keys/privateKey.pem
mv /etc/renthome/keys/publicKey.pem.new /etc/renthome/keys/publicKey.pem
```

---

## 🛡️ Checklist de Seguridad

- [ ] ✅ Keys fuera del repositorio Git
- [ ] ✅ `.gitignore` configurado para excluir `*.pem`
- [ ] ✅ Variables de entorno configuradas
- [ ] ✅ Permisos restrictivos en archivos (600 para private, 644 para public)
- [ ] ✅ Keys diferentes para dev/staging/prod
- [ ] ✅ Backup de keys de producción
- [ ] ✅ Plan de rotación de keys
- [ ] ✅ Monitoreo de uso de keys

---

## 🚨 En Caso de Compromiso

1. **Inmediato**: Generar nuevas keys
2. **Invalidar**: Todos los tokens existentes
3. **Reiniciar**: Aplicación con nuevas keys
4. **Auditar**: Logs de acceso
5. **Notificar**: Usuarios si es necesario

---

## 📚 Referencias

- [Quarkus JWT Security](https://quarkus.io/guides/security-jwt)
- [SmallRye JWT](https://smallrye.io/smallrye-jwt/)
- [OpenSSL Key Generation](https://www.openssl.org/docs/man1.1.1/man1/openssl-genrsa.html)
