# RentHome - Sistema de Gestión de Alquileres

## Descripción General

RentHome es un sistema distribuido de gestión de alquileres de propiedades desarrollado con Spring Boot y arquitectura de microservicios. El sistema permite la administración completa de casas, unidades habitacionales, inquilinos, contratos y facturación.

## Arquitectura del Sistema

El proyecto está estructurado en 4 microservicios principales:

```
RentHome
├── renthome-eureka     (Servidor de descubrimiento)
├── renthome-gateway    (API Gateway)
├── renthome-security   (Servicio de autenticación y autorización)
└── renthome-core       (Servicio principal de negocio)
```

## Microservicios

### 1. RentHome Eureka (`renthome-eureka`)
**Puerto:** 8761  
**Descripción:** Servidor de descubrimiento de servicios basado en Netflix Eureka

**Tecnologías:**
- Spring Boot 3.3.5
- Spring Cloud 2023.0.6
- Netflix Eureka Server
- Java 21

**Configuración:**
```yaml
spring:
  application:
    name: renthome-eureka
server:
  port: 8761
eureka:
  instance:
    hostname: localhost
```

**Función:** Permite el registro y descubrimiento automático de todos los microservicios del sistema.

### 2. RentHome Gateway (`renthome-gateway`)
**Puerto:** 8099  
**Descripción:** API Gateway que actúa como punto de entrada único al sistema

**Tecnologías:**
- Spring Boot 3.3.5
- Spring Cloud Gateway
- Netflix Eureka Client
- Java 21

**Rutas configuradas:**
- `/v1/**` → `renthome-core` (Servicios principales)
- `/auth/**` → `renthome-security` (Autenticación)

**Configuración:**
```yaml
spring:
  application:
    name: renthome-gateway
  cloud:
    gateway:
      routes:
      - id: core
        uri: lb://renthome-core
        predicates:
          - Path=/v1/**
      - id: auth
        uri: lb://renthome-security
        predicates:
          - Path=/auth/**
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
server:
  port: 8099
```

**Funciones:**
- Enrutamiento inteligente de solicitudes
- Balanceo de carga automático
- Punto de entrada único para clientes externos

### 3. RentHome Security (`renthome-security`)
**Puerto:** 9999  
**Descripción:** Servicio de autenticación y autorización con JWT

**Tecnologías:**
- Spring Boot 3.3.5
- Spring Security
- JWT (JSON Web Tokens) - jjwt 0.12.3
- Spring Data JPA
- MySQL
- HATEOAS
- Java 21

**Funcionalidades:**
- Autenticación de usuarios
- Generación y validación de tokens JWT
- Gestión de usuarios y roles
- Autorización basada en roles

**Entidades principales:**
- `Usuario`: Gestión de usuarios del sistema
- `Token`: Gestión de tokens JWT (access y refresh)

**Configuración JWT:**
- Access Token: 12 horas (43200000 ms)
- Refresh Token: 24 horas (86400000 ms)
- Algoritmo: HS256/HS384/HS512 (auto-detectado por longitud de clave)

**Endpoints principales:**
- `POST /auth/login` - Autenticación de usuarios

**Configuración:**
```yaml
spring:
  application:
    name: renthome-security
  datasource:
    url: jdbc:mysql://${HOST_BD}:${PORT_BD}/${NAME_BD}
    username: ${USER_BD}
    password: ${PASSWORD_BD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
server:
  port: 9999
security:
  jwt:
    secret-key: ${JWT_SECRET_KEY}
    access-token-expiration: 43200000
    refresh-token-expiration: 86400000
```

### 4. RentHome Core (`renthome-core`)
**Puerto:** 8082  
**Descripción:** Servicio principal que contiene la lógica de negocio

**Tecnologías:**
- Spring Boot 3.3.5
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT (JSON Web Tokens) - jjwt 0.12.3
- MySQL
- HATEOAS
- Java 21

**Entidades del modelo de datos:**
- `Casa`: Propiedades inmobiliarias (nombre, dirección, relación con unidades y usuarios)
- `UnidadHabitacional`: Unidades dentro de las casas (nombre, servicios incluidos, tipo, estado)
- `Inquilino`: Información de inquilinos (nombre completo, documento, teléfono, correo)
- `Contrato`: Contratos de alquiler (fechas, monto, garantía, estado, inquilino, unidad)
- `Recibo`: Recibos de pago (relación con contratos y detalles)
- `DetalleRecibo`: Detalles de los recibos
- `TarifaServicio`: Tarifas de servicios adicionales
- `Usuario`: Usuarios del sistema (nombre, email, password, estado activo)
- `Token`: Tokens JWT para gestión de sesiones

**Funcionalidades:**
- Gestión de propiedades inmobiliarias
- Administración de inquilinos
- Gestión de contratos de alquiler
- Sistema de facturación y recibos
- Gestión de servicios adicionales
- Validación de tokens JWT como OAuth2 Resource Server

**Endpoints principales:**
- `GET /v1/inquilinos` - Listar inquilinos (paginado)
- `POST /v1/inquilinos` - Registrar nuevo inquilino
- `PUT /v1/inquilinos` - Modificar inquilino
- `DELETE /v1/inquilinos/{id}` - Eliminar inquilino
- `GET /v1/inquilinos/{id}` - Obtener inquilino por ID

**Configuración:**
```yaml
spring:
  application:
    name: renthome-core
  datasource:
    url: jdbc:mysql://${HOST_BD}:${PORT_BD}/${NAME_BD}
    username: ${USER_BD}
    password: ${PASSWORD_BD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
server:
  port: 8082
security:
  jwt:
    secret-key: ${JWT_SECRET_KEY}
```

## Configuración del Sistema

### Base de Datos
Todos los servicios utilizan MySQL como sistema de gestión de base de datos:

**Configuración YAML (renthome-core, renthome-security, renthome-eureka):**
```yaml
spring:
  config:
    import: optional:file:.env[.properties]
  datasource:
    url: jdbc:mysql://${HOST_BD}:${PORT_BD}/${NAME_BD}
    username: ${USER_BD}
    password: ${PASSWORD_BD}
  jpa:
    database: mysql
    hibernate:
      ddl-auto: update
    show-sql: false
```

### Variables de Entorno Requeridas

El sistema requiere las siguientes variables de entorno configuradas en un archivo `.env`:

```properties
# Base de datos
HOST_BD=localhost
PORT_BD=3306
NAME_BD=renthome_db
USER_BD=tu_usuario
PASSWORD_BD=tu_contraseña

# JWT Security
JWT_SECRET_KEY=tu_clave_secreta_jwt_base64url
```

### Configuración JWT

**Importante:** La clave secreta JWT debe ser una cadena codificada en BASE64URL:
- Mínimo 256 bits (32 bytes) para HS256
- Mínimo 384 bits (48 bytes) para HS384  
- Mínimo 512 bits (64 bytes) para HS512

El algoritmo se selecciona automáticamente basado en la longitud de la clave decodificada.

## Puertos por Defecto

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| renthome-eureka | 8761 | Servidor de descubrimiento |
| renthome-gateway | 8099 | API Gateway |
| renthome-security | 9999 | Servicio de autenticación |
| renthome-core | 8082 | Servicio principal |

## Flujo de Comunicación

1. **Registro de Servicios**: Todos los microservicios se registran en Eureka Server
2. **Descubrimiento**: El Gateway descubre los servicios disponibles a través de Eureka
3. **Enrutamiento**: Las solicitudes del cliente llegan al Gateway y son enrutadas al servicio correspondiente
4. **Autenticación**: Las solicitudes de autenticación son procesadas por renthome-security
5. **Validación JWT**: renthome-core valida los tokens JWT como OAuth2 Resource Server
6. **Lógica de Negocio**: Las operaciones principales son procesadas por renthome-core

## API Endpoints

### Autenticación (`/auth/*`)
Procesados por `renthome-security`:
- `POST /auth/login` - Autenticación de usuarios
  - Request: `{"email": "usuario@ejemplo.com", "password": "contraseña"}`
  - Response: `{"accessToken": "...", "refreshToken": "..."}`

### Core Business (`/v1/*`)
Procesados por `renthome-core` (requieren autenticación JWT):
- `GET /v1/inquilinos` - Listar inquilinos (paginado)
- `POST /v1/inquilinos` - Registrar nuevo inquilino
- `PUT /v1/inquilinos` - Modificar inquilino
- `DELETE /v1/inquilinos/{id}` - Eliminar inquilino
- `GET /v1/inquilinos/{id}` - Obtener inquilino por ID

**Ejemplo de uso:**
```bash
# 1. Obtener token
curl -X POST http://localhost:8099/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@ejemplo.com", "password": "contraseña"}'

# 2. Usar token para acceder a endpoints protegidos
curl -X GET http://localhost:8099/v1/inquilinos \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## Características Técnicas

### Seguridad
- Autenticación JWT con tokens de acceso y refresh
- OAuth2 Resource Server en renthome-core
- Spring Security en todos los servicios
- Algoritmo HMAC automático (HS256/HS384/HS512)
- Decodificación BASE64URL consistente

### Persistencia
- Spring Data JPA con repositorios
- MySQL como base de datos principal
- Hibernate con DDL auto-update
- Entidades JPA con relaciones (@ManyToOne, @OneToMany, @ManyToMany)

### Arquitectura
- Microservicios con Spring Cloud
- Service Discovery con Netflix Eureka
- API Gateway con Spring Cloud Gateway
- Load Balancing automático con Ribbon
- Configuración centralizada con archivos .env

### Monitoreo y Desarrollo
- Spring Boot DevTools para desarrollo
- Logging con SLF4J y Logback
- HATEOAS para APIs RESTful
- Actuator endpoints para monitoreo

## Dependencias Principales

### renthome-eureka
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### renthome-gateway
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### renthome-security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<!-- + jjwt-impl, jjwt-jackson -->
```

### renthome-core
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<!-- + jjwt-impl, jjwt-jackson -->
```

## Requisitos del Sistema

- **Java:** 21+
- **Maven:** 3.6+
- **MySQL:** 8.0+
- **Spring Boot:** 3.3.5
- **Spring Cloud:** 2023.0.6 (renthome-core, renthome-security, renthome-eureka)
- **Spring Cloud:** 2023.0.3 (renthome-gateway)

## Orden de Arranque

Para ejecutar el sistema correctamente, seguir este orden:

1. **MySQL** - Crear base de datos
2. **renthome-eureka** (Puerto 8761) - Servidor de descubrimiento
3. **renthome-security** (Puerto 9999) - Servicio de autenticación
4. **renthome-core** (Puerto 8082) - Servicio principal
5. **renthome-gateway** (Puerto 8099) - API Gateway

**Comandos Maven:**
```bash
# 1. Eureka Server
cd renthome-eureka
mvn spring-boot:run

# 2. Security Service
cd renthome-security
mvn spring-boot:run

# 3. Core Service
cd renthome-core
mvn spring-boot:run

# 4. Gateway
cd renthome-gateway
mvn spring-boot:run
```

## Estructura del Proyecto

```
renthome/
├── renthome-eureka/
│   ├── src/main/java/com/surrender/
│   │   └── RenthomeEurekaApplication.java
│   └── src/main/resources/application.yaml
├── renthome-gateway/
│   ├── src/main/java/com/surrender/
│   │   └── RenthomeGatewayApplication.java
│   └── src/main/resources/application.yaml
├── renthome-security/
│   ├── src/main/java/com/surrender/
│   │   ├── controller/AuthenticationController.java
│   │   ├── service/impl/AuthenticationService.java
│   │   ├── service/impl/JwtService.java
│   │   ├── model/Usuario.java
│   │   ├── model/Token.java
│   │   ├── repo/UsuarioRepo.java
│   │   ├── repo/TokenRepo.java
│   │   ├── security/
│   │   └── RenthomeSecurityApplication.java
│   └── src/main/resources/application.yaml
└── renthome-core/
    ├── src/main/java/com/surrender/
    │   ├── controller/InquilinoController.java
    │   ├── service/IInquilinoService.java
    │   ├── service/impl/InquilinoService.java
    │   ├── model/Casa.java
    │   ├── model/UnidadHabitacional.java
    │   ├── model/Inquilino.java
    │   ├── model/Contrato.java
    │   ├── model/Recibo.java
    │   ├── model/DetalleRecibo.java
    │   ├── model/TarifaServicio.java
    │   ├── model/Usuario.java
    │   ├── model/Token.java
    │   ├── repo/
    │   ├── dto/APIResponseDTO.java
    │   ├── configuration/SecurityConfig.java
    │   └── RenthomeCoreApplication.java
    └── src/main/resources/application.yaml
```

## Configuración de Seguridad

### renthome-security (Authorization Server)
- Genera tokens JWT usando jjwt
- Usa BASE64URL decoding para la clave secreta
- Algoritmo HMAC auto-detectado por longitud de clave
- Autentica usuarios contra base de datos MySQL

### renthome-core (Resource Server)
- Valida tokens JWT como OAuth2 Resource Server
- Usa el mismo método de decodificación BASE64URL
- Algoritmo HMAC auto-detectado para consistencia
- Protege endpoints `/v1/**` con autenticación JWT

### Configuración SecurityConfig en renthome-core
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/v1/**").authenticated()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.decoder(jwtDecoder()))
        )
        .build();
}
```

## Modelo de Datos

### Entidades Principales

**Casa:**
- id (Integer, PK)
- nombre (String, NOT NULL)
- direccion (String, NOT NULL)
- unidades (List<UnidadHabitacional>, @OneToMany)
- usuarios (List<Usuario>, @ManyToMany)

**UnidadHabitacional:**
- id (Integer, PK)
- nombre (String, NOT NULL)
- incluyeAgua (boolean, NOT NULL)
- incluyeLuz (boolean, NOT NULL)
- tipoUnidad (String, NOT NULL)
- estado (String, NOT NULL)
- casa (Casa, @ManyToOne)
- contratos (List<Contrato>, @OneToMany)

**Inquilino:**
- id (Integer, PK)
- nombreCompleto (String, NOT NULL)
- documentoIdentidad (String, NOT NULL)
- telefono (String, NOT NULL)
- correo (String, NOT NULL)

**Contrato:**
- id (Integer, PK)
- fechaInicio (LocalDate, NOT NULL)
- fechaFin (LocalDate, nullable)
- montoRentaMensual (float, NOT NULL)
- garantia (float, NOT NULL)
- activo (boolean, NOT NULL)
- inquilino (Inquilino, @ManyToOne)
- unidad (UnidadHabitacional, @ManyToOne)
- recibos (List<Recibo>, @OneToMany)

**Usuario:**
- id (Integer, PK)
- nombre (String, NOT NULL)
- email (String, NOT NULL)
- password (String, NOT NULL)
- activo (boolean, NOT NULL)
- casas (List<Casa>, @ManyToMany)

**Token:**
- id (Integer, PK)
- accessToken (String)
- refreshToken (String)
- loggedOut (boolean)
- usuario (Usuario, @ManyToOne)

## Notas de Desarrollo

- El sistema utiliza Java 21 como versión mínima
- Se recomienda usar un archivo `.env` para las variables de entorno
- Los servicios utilizan Spring Boot DevTools para desarrollo
- La base de datos se actualiza automáticamente con Hibernate DDL
- Todos los servicios implementan logging con SLF4J
- Los endpoints están protegidos con JWT y OAuth2 Resource Server

## Próximos Pasos

1. **Configurar variables de entorno** en archivo `.env`
2. **Crear la base de datos MySQL** con el nombre especificado
3. **Ejecutar los servicios** en el orden especificado
4. **Verificar el registro de servicios** en Eureka (http://localhost:8761)
5. **Probar los endpoints** a través del Gateway (http://localhost:8099)
6. **Implementar controladores adicionales** para las demás entidades
7. **Agregar validaciones** y manejo de errores
8. **Implementar tests unitarios** e integración
9. **Configurar monitoreo** con Actuator
10. **Documentar APIs** con OpenAPI/Swagger

## Solución de Problemas Comunes

### Problema: Error "Another algorithm expected" en JWT

**Síntoma:** Error 401 con mensaje `"Another algorithm expected, or no matching key(s) found"`

**Causa:** Discrepancia entre el algoritmo de firma JWT usado por `renthome-security` y el algoritmo esperado por `renthome-core`. 

**Solución:** ✅ **RESUELTO**
- Configurado el `JwtDecoder` en `renthome-core` para detectar automáticamente el algoritmo
- Ambos servicios usan el mismo método de decodificación BASE64URL
- Algoritmo se selecciona automáticamente basado en la longitud de la clave

### Problema: "Pantalla de login al acceder a endpoints protegidos"

**Síntoma:** El login en `/auth/login` funciona y devuelve tokens JWT, pero al acceder a endpoints como `/v1/inquilinos` con el token Bearer aparece una pantalla de login.

**Causa:** El servicio `renthome-core` no estaba configurado correctamente como OAuth2 Resource Server.

**Solución:** ✅ **RESUELTO**
- Configurado `SecurityConfig` en `renthome-core` como OAuth2 Resource Server
- Agregadas las dependencias JWT (jjwt-api, jjwt-impl, jjwt-jackson) a `renthome-core`
- Agregada la configuración JWT en `application.yaml` de `renthome-core`
- Implementado `JwtDecoder` personalizado con decodificación BASE64URL

### Problema: Servicios no se registran en Eureka

**Síntoma:** Los servicios no aparecen en el dashboard de Eureka.

**Solución:**
1. Verificar que Eureka Server esté ejecutándose en puerto 8761
2. Revisar la configuración de Eureka Client en cada servicio
3. Verificar conectividad de red
4. Revisar logs de cada servicio para errores de registro

### Problema: Gateway no rutea correctamente

**Síntoma:** Error 404 al acceder a través del Gateway.

**Solución:**
1. Verificar configuración de rutas en `application.yaml`
2. Confirmar que los servicios estén registrados en Eureka
3. Verificar que el Gateway esté configurado como Eureka Client
4. Revisar logs del Gateway para errores de enrutamiento

### Problema: Error de conexión a base de datos

**Síntoma:** Servicios no pueden conectarse a MySQL.

**Solución:**
1. Verificar que MySQL esté ejecutándose
2. Confirmar variables de entorno en archivo `.env`
3. Verificar permisos de usuario en MySQL
4. Confirmar que la base de datos existe
5. Revisar logs para errores específicos de conexión

### Verificación del Sistema

**1. Verificar Eureka Server:**
```bash
curl http://localhost:8761/eureka/apps
```

**2. Verificar autenticación:**
```bash
curl -X POST http://localhost:8099/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@ejemplo.com", "password": "contraseña"}'
```

**3. Verificar endpoints protegidos:**
```bash
curl -X GET http://localhost:8099/v1/inquilinos \
  -H "Authorization: Bearer [TOKEN]"
```

**4. Verificar logs:**
- Revisar logs de cada servicio para errores
- Verificar que JWT tokens se generen y validen correctamente
- Confirmar que los servicios se registren en Eureka

## Estado Actual del Sistema

✅ **Sistema completamente funcional**
- Autenticación JWT funcionando correctamente
- Validación de tokens en renthome-core implementada
- Endpoints protegidos accesibles con tokens válidos
- Servicios registrados en Eureka correctamente
- Gateway rutea correctamente las peticiones

⚠️ **Pendiente de implementar:**
- Controladores adicionales para las demás entidades
- Validaciones de entrada y manejo de errores
- Tests unitarios e integración
- Documentación OpenAPI/Swagger
- Monitoreo y métricas avanzadas
