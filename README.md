# RentHome - Sistema de Gestión de Alquileres de Propiedades

## Descripción General

RentHome es un sistema completo de gestión de alquileres de propiedades inmobiliarias desarrollado en Java con Spring Boot. El proyecto incluye dos implementaciones arquitectónicas:

1. **Arquitectura Monolítica** - Aplicación única con todas las funcionalidades integradas
2. **Arquitectura de Microservicios** - Sistema distribuido con servicios especializados

Ambas implementaciones proporcionan la funcionalidad completa para administrar casas, unidades habitacionales, inquilinos, contratos de alquiler, facturación y autenticación de usuarios.

## Estructura del Proyecto

```
renthome-back/
├── monolito/           # Implementación monolítica
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yaml
│   └── README.md       # Documentación detallada del monolito
├── microservices/      # Implementación con microservicios
│   ├── renthome-eureka/     # Servidor de descubrimiento
│   ├── renthome-gateway/    # API Gateway
│   ├── renthome-security/   # Servicio de autenticación
│   ├── renthome-core/       # Servicio principal de negocio
│   └── README.md            # Documentación detallada de microservicios
└── README.md           # Este archivo
```

## Arquitecturas Disponibles

### 🏗️ Monolito
- **Puerto:** 8080
- **Descripción:** Aplicación única que contiene toda la lógica de negocio
- **Ideal para:** Desarrollo rápido, despliegue simple, equipos pequeños
- **Documentación completa:** [📖 monolito/README.md](./monolito/README.md)

**Características principales:**
- Spring Boot con Spring Security
- Autenticación JWT integrada
- API REST completa
- Base de datos MySQL
- Configuración CORS global
- Logging con SLF4J
- Respuestas unificadas con APIResponseDTO

### 🔧 Microservicios
- **Puertos:** 8761 (Eureka), 8099 (Gateway), 9999 (Security), 8082 (Core)
- **Descripción:** Sistema distribuido con servicios especializados
- **Ideal para:** Escalabilidad, equipos grandes, arquitectura empresarial
- **Documentación completa:** [📖 microservices/README.md](./microservices/README.md)

**Componentes:**
- **renthome-eureka** (8761): Servidor de descubrimiento Netflix Eureka
- **renthome-gateway** (8099): API Gateway con Spring Cloud Gateway
- **renthome-security** (9999): Servicio de autenticación JWT
- **renthome-core** (8082): Lógica de negocio principal como OAuth2 Resource Server

## Tecnologías Utilizadas

| Tecnología | Monolito | Microservicios |
|------------|----------|----------------|
| **Java** | 21 | 21 |
| **Spring Boot** | 3.x | 3.3.5 |
| **Spring Security** | ✅ | ✅ |
| **Spring Data JPA** | ✅ | ✅ |
| **MySQL** | ✅ | ✅ |
| **JWT** | ✅ | ✅ |
| **Spring Cloud** | ❌ | ✅ (2023.0.6) |
| **Netflix Eureka** | ❌ | ✅ |
| **API Gateway** | ❌ | ✅ |
| **OAuth2 Resource Server** | ❌ | ✅ |
| **HATEOAS** | ✅ | ✅ |
| **Docker** | ✅ | ❌ |

## Modelo de Datos

Ambas implementaciones comparten el mismo modelo de datos:

### Entidades Principales
- **Casa**: Propiedades inmobiliarias (nombre, dirección)
- **UnidadHabitacional**: Unidades dentro de las casas (departamentos, habitaciones)
- **Inquilino**: Información de inquilinos (datos personales y contacto)
- **Contrato**: Contratos de alquiler (fechas, montos, garantías)
- **Recibo**: Recibos de pago mensual
- **DetalleRecibo**: Detalles específicos de cada recibo
- **TarifaServicio**: Tarifas de servicios adicionales (agua, luz, etc.)
- **Usuario**: Usuarios del sistema con autenticación
- **Token**: Gestión de sesiones JWT

### Relaciones
- Casa → UnidadHabitacional (1:N)
- UnidadHabitacional → Contrato (1:N)
- Inquilino → Contrato (1:N)
- Contrato → Recibo (1:N)
- Recibo → DetalleRecibo (1:N)
- Casa → TarifaServicio (1:N)
- Usuario → Token (1:N)

## Configuración Rápida

### Variables de Entorno Requeridas

Crear archivo `.env` en la raíz del proyecto (monolito) o en cada microservicio:

```properties
# Base de datos MySQL
HOST_BD=localhost
PORT_BD=3306
NAME_BD=renthome_db
USER_BD=tu_usuario
PASSWORD_BD=tu_contraseña

# JWT Security
JWT_SECRET_KEY=tu_clave_secreta_jwt_base64url_minimo_32_caracteres
```

### Ejecución - Monolito

```bash
cd monolito
./mvnw spring-boot:run
```
**Acceso:** http://localhost:8080

### Ejecución - Microservicios

**Orden de arranque:**
```bash
# 1. Eureka Server
cd microservices/renthome-eureka
mvn spring-boot:run

# 2. Security Service
cd microservices/renthome-security
mvn spring-boot:run

# 3. Core Service
cd microservices/renthome-core
mvn spring-boot:run

# 4. Gateway
cd microservices/renthome-gateway
mvn spring-boot:run
```

**Acceso:**
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8099

## API Endpoints

### Autenticación (Ambas arquitecturas)

```bash
# Monolito
POST http://localhost:8080/v1/auth/login

# Microservicios
POST http://localhost:8099/auth/login
```

### Endpoints Protegidos

```bash
# Monolito
GET http://localhost:8080/v1/inquilinos
Authorization: Bearer {token}

# Microservicios
GET http://localhost:8099/v1/inquilinos
Authorization: Bearer {token}
```

## Comparación de Arquitecturas

| Aspecto | Monolito | Microservicios |
|---------|----------|----------------|
| **Complejidad** | Baja | Alta |
| **Desarrollo inicial** | Rápido | Lento |
| **Mantenimiento** | Simple | Complejo |
| **Escalabilidad** | Vertical | Horizontal |
| **Tolerancia a fallos** | Baja | Alta |
| **Despliegue** | Simple | Complejo |
| **Monitoreo** | Simple | Complejo |
| **Transacciones** | ACID simple | Eventual consistency |
| **Debugging** | Fácil | Difícil |

## Requisitos del Sistema

- **Java:** 21+
- **Maven:** 3.6+
- **MySQL:** 8.0+
- **Memoria RAM:** 2GB mínimo (monolito), 4GB recomendado (microservicios)
- **Espacio en disco:** 1GB

## Elección de Arquitectura

### Usar Monolito cuando:
- ✅ Equipo pequeño (< 5 desarrolladores)
- ✅ Proyecto en fase inicial o MVP
- ✅ Requisitos simples de escalabilidad
- ✅ Presupuesto limitado de infraestructura
- ✅ Necesitas desarrollo y despliegue rápido

### Usar Microservicios cuando:
- ✅ Equipo grande (> 10 desarrolladores)
- ✅ Aplicación madura con dominios complejos
- ✅ Necesitas escalabilidad horizontal
- ✅ Diferentes tecnologías por servicio
- ✅ Alta disponibilidad es crítica
- ✅ Equipos autónomos por dominio

## Documentación Detallada

Para información específica de implementación, configuración avanzada, patrones utilizados y resolución de problemas, consultar:

- **[📖 Documentación del Monolito](./monolito/README.md)** - Guía completa de la implementación monolítica
- **[📖 Documentación de Microservicios](./microservices/README.md)** - Guía completa de la arquitectura distribuida

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

---

**Desarrollado con ❤️ usando Spring Boot y Java 21**
