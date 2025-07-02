# Renthome Back

## Descripción general
Este proyecto es el backend de una aplicación de gestión de alquileres de viviendas, desarrollado en Java utilizando Spring Boot. Permite administrar casas, unidades habitacionales, contratos de alquiler, inquilinos, recibos, tarifas de servicios, usuarios y tokens de sesión.

## Estructura del proyecto
- **Spring Boot**: Framework principal para la creación de aplicaciones web y servicios REST.
- **JPA/Hibernate**: Para la persistencia de datos y mapeo objeto-relacional.
- **MySQL**: Base de datos relacional utilizada.
- **Maven**: Herramienta de gestión y construcción del proyecto.

### Entidades principales
- **Casa**: Representa una propiedad con dirección y nombre, puede tener varias unidades habitacionales y usuarios asociados.
- **UnidadHabitacional**: Unidad dentro de una casa (ej. departamento), con atributos como nombre, tipo, estado y servicios incluidos.
- **Contrato**: Relaciona un inquilino con una unidad habitacional, incluye fechas, monto de renta, garantía y estado.
- **Inquilino**: Persona que alquila una unidad, con datos personales y de contacto.
- **Recibo**: Documento generado por cada periodo de alquiler, con monto total, estado de pago y detalles.
- **DetalleRecibo**: Detalle de cada recibo, con descripción y monto.
- **TarifaServicio**: Tarifas de servicios (agua, luz, etc.) asociadas a una casa.
- **Usuario**: Usuarios del sistema, con autenticación básica y relación con casas.
- **Token**: Guarda los JWT generados cuando un usuario inicia sesión. Sus atributos son: id, accessToken, refreshToken, loggedOut, usuario.

## Repositorios y acceso a datos
- Se implementó una interfaz genérica `IGenericRepo<T, ID>` en el paquete `repo`, que extiende de `JpaRepository` y sirve como base para los repositorios específicos. Esta interfaz está anotada con `@NoRepositoryBean` para evitar que Spring la registre como un bean directo.
- Los repositorios `UsuarioRepo`, `InquilinoRepo` y `TokenRepo` extienden de `IGenericRepo` para facilitar la reutilización y mantener un acceso a datos consistente. Todos exponen métodos para paginación con `findAll(Pageable pageable)`.

## Servicios y lógica de negocio
- Existe una interfaz genérica `ICRUD<T, ID>` en el paquete `service` que define los métodos básicos para operaciones CRUD: registrar, modificar, eliminar, listar por ID, listar todos y filtrar (paginación).
- Las interfaces de servicio específicas (`IUsuarioService`, `IInquilinoService`, `ITokenService`, etc.) extienden de `ICRUD` y siguen la convención de prefijo `I` y sufijo `Service`.
- En el paquete `service.impl` se encuentra la clase abstracta `CRUDImpl<T, ID>`, que implementa la lógica genérica de los métodos CRUD y paginación usando el repositorio correspondiente.
- Las implementaciones concretas (`UsuarioServiceImpl`, `InquilinoServiceImpl`, `TokenServiceImpl`) extienden de `CRUDImpl` y definen el repositorio a utilizar.

### Lógica de modificación de usuario

En `UsuarioServiceImpl`, el método `modificar(Usuario usuario)` implementa la lógica para actualizar un usuario existente:
- Busca el usuario por ID.
- Si existe, mantiene la contraseña original (no permite cambiarla ni la recodifica desde este endpoint) y actualiza el resto de los datos.
- Se registran logs informativos y de advertencia según el resultado.

**Ejemplo de lógica aplicada:**
```java
@Override
public Usuario modificar(Usuario usuario) {
    logger.info("Modificando usuario con id: {}", usuario.getId());
    Optional<Usuario> existe = usuarioRepo.findById(usuario.getId());
    if (existe.isPresent()) {
        Usuario usuarioExistente = existe.get();
        usuario.setPassword(usuarioExistente.getPassword());
        Usuario actualizado = usuarioRepo.save(usuario);
        logger.info("Usuario modificado exitosamente: {}", actualizado.getEmail());
        return actualizado;
    } else {
        logger.warn("Intento de modificación fallido: usuario con id {} no encontrado", usuario.getId());
        return null;
    }
}
```

## DTO de respuesta unificada
- Se creó el DTO genérico `APIResponseDTO<T>` en el paquete `dto`, que será la única forma de respuesta para todos los controllers.
- Sus campos son:
  - `boolean success`: Indica si la operación fue exitosa.
  - `String message`: Mensaje descriptivo de la respuesta.
  - `T data`: Datos devueltos por la operación.
  - `int statusCode`: Código de estado HTTP asociado.
  - `LocalDateTime timestamp`: Fecha y hora de la respuesta.

### Respuestas unificadas con APIResponseDTO

Para garantizar una estructura de respuesta consistente en toda la API, se utiliza el DTO genérico `APIResponseDTO<T>`. Este DTO incluye métodos estáticos de conveniencia:

- `APIResponseDTO.success(String message, T data, int statusCode)`: genera una respuesta exitosa con mensaje, datos y código de estado.
- `APIResponseDTO.error(String message, int statusCode)`: genera una respuesta de error con mensaje y código de estado, sin datos.

Esto permite a los controladores construir respuestas limpias y estandarizadas, facilitando el consumo y manejo de errores en el frontend y la mantenibilidad del backend.

**Ejemplo de uso:**
```java
return APIResponseDTO.success("Usuario creado", usuario, 201);
return APIResponseDTO.error("No autorizado", 401);
```

## Controladores REST
- Los controladores `UsuarioController` e `InquilinoController` se encuentran en el paquete `controller`.
- Todos los endpoints están bajo el prefijo `/v1` (por ejemplo, `/v1/usuarios`, `/v1/inquilinos`).
- Todas las respuestas utilizan el DTO `APIResponseDTO` para unificar la estructura de respuesta.
- Los controladores implementan operaciones CRUD completas para sus respectivas entidades.
- Ambos controladores exponen un endpoint `/filtrar` que permite obtener resultados paginados usando parámetros de Spring Data (`Pageable`).
- La gestión de tokens se realiza a través del controlador de usuario, no existe un controlador dedicado para tokens.

### Patrón de controladores y servicios (actualizado)

A partir de la refactorización aplicada, la arquitectura de controladores y servicios sigue este patrón:

- **Servicios**: Devuelven objetos de dominio o DTOs de negocio (por ejemplo, `Usuario`, `Inquilino`, `Page<Usuario>`, etc.), nunca un `APIResponseDTO`. Toda la lógica de negocio, validación y acceso a datos se realiza aquí.
- **Controladores**: Reciben el resultado del servicio y construyen el `APIResponseDTO` usando los métodos estáticos `success` y `error`. El controlador es responsable de asignar el código de estado HTTP y envolver la respuesta en un `ResponseEntity`.
- **Ventajas**: 
  - Separación clara de responsabilidades: la lógica de negocio no depende de la presentación.
  - Controladores delgados y fáciles de mantener.
  - Respuestas unificadas y consistentes en toda la API.

#### Ejemplo de patrón aplicado
```java
// Servicio
public Inquilino registrar(Inquilino inquilino) { ... }

// Controlador
@PostMapping
public ResponseEntity<APIResponseDTO<Inquilino>> registrar(@RequestBody Inquilino inquilino) {
    Inquilino nuevo = inquilinoService.registrar(inquilino);
    APIResponseDTO<Inquilino> response = (nuevo != null)
        ? APIResponseDTO.success("Inquilino registrado", nuevo, 201)
        : APIResponseDTO.error("No se pudo registrar el inquilino", 400);
    return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
}
```

#### Notas adicionales
- El DTO `APIResponseDTO` centraliza la estructura de respuesta y expone métodos estáticos para construir respuestas de éxito o error.
- El código de estado HTTP se asigna en el controlador según el resultado de la operación.
- Este patrón se aplica a todos los controladores principales (`UsuarioController`, `InquilinoController`, etc.).

---
Generado automáticamente a partir del análisis del código fuente y archivos de configuración.

## Configuración
- **application.properties**: Configuración de la aplicación, incluyendo conexión a la base de datos mediante variables de entorno.
- **.env**: Variables de entorno para la conexión a MySQL (host, puerto, nombre de la base, usuario y contraseña).

## Dependencias principales
- `spring-boot-starter-web`: Para exponer servicios REST.
- `spring-boot-starter-data-jpa`: Para persistencia de datos.
- `mysql-connector-j`: Driver para MySQL.
- `spring-boot-starter-hateoas`: Para hipermedios en las respuestas REST.
- `spring-boot-starter-test`: Para pruebas.
- `org.slf4j:slf4j-api`: API de logging utilizada en todo el backend.

## Ejecución
1. Configura las variables de entorno en el archivo `.env`.
2. Ejecuta el proyecto con Maven Wrapper:
   ```sh
   ./mvnw spring-boot:run
   ```

## Seguridad y Autenticación

El proyecto implementa seguridad robusta basada en JWT y Spring Security, siguiendo buenas prácticas y una arquitectura desacoplada y escalable.

### Componentes principales de seguridad

- **Spring Security**: Se utiliza para proteger los endpoints REST y gestionar la autenticación y autorización.
- **JWT (JSON Web Token)**: Los tokens se generan al autenticar un usuario y se almacenan en la base de datos (entidad `Token`).
- **PasswordEncoder**: Las contraseñas de los usuarios se almacenan de forma segura usando BCrypt.
- **Filtros y handlers personalizados**:
  - `JwtAuthenticationFilter`: Filtro que intercepta cada petición HTTP, valida el JWT y establece el usuario autenticado en el contexto de seguridad.
  - `CustomLogoutHandler`: Handler que marca el token como cerrado (logged out) en la base de datos al hacer logout.
- **UserDetailsServiceImpl**: Implementación personalizada de `UserDetailsService` que permite a Spring Security cargar usuarios desde la base de datos usando el email.
- **Configuración centralizada**:
  - `SecurityConfig`: Define la cadena de filtros, endpoints públicos, manejo de sesión stateless, integración del filtro JWT y logout seguro.
  - Inyección de dependencias por constructor para mayor claridad y testabilidad.

### Flujo de autenticación y autorización

1. **Login**: El usuario envía sus credenciales a `/v1/auth/login`. Si son válidas, se genera un JWT y se almacena en la tabla `Token`.
2. **Acceso a endpoints protegidos**: El cliente debe enviar el JWT en la cabecera `Authorization: Bearer <token>`. El filtro `JwtAuthenticationFilter` valida el token y autentica al usuario.
3. **Logout**: Al llamar a `/logout`, el `CustomLogoutHandler` marca el token como cerrado en la base de datos, invalidando su uso futuro.
4. **Gestión de contraseñas**: Las contraseñas se almacenan siempre encriptadas con BCrypt.

### Endpoints públicos y protegidos
- **Públicos**: `/v1/auth/login/**`, `/v1/auth/recover_password/**`, `/v1/auth/reset_password/**`, `/v1/usuarios/**`
- **Protegidos**: Todos los demás requieren JWT válido.

### Configuración de propiedades
En `application.properties`:
```
security.jwt.secret-key=${JWT_SECRET_KEY}
security.jwt.access-token-expiration=43200000
security.jwt.refresh-token-expiration=86400000
```

### Paquetes y clases relevantes
- `com.surrender.security`: Filtros y handlers de seguridad personalizados.
- `com.surrender.service.impl`: Servicios de autenticación y JWT.
- `com.surrender.configuration.SecurityConfig`: Configuración principal de seguridad.

### Buenas prácticas aplicadas
- Inyección de dependencias por constructor en componentes de seguridad.
- Clases de filtro y handler marcadas como `final` (excepto `@Configuration`).
- Comentarios clave en los puntos críticos del flujo de seguridad.
- Cohesión de clases y separación clara de responsabilidades.
- **Logging profesional**: Todos los controladores, servicios y componentes de seguridad implementan SLF4J para trazabilidad y diagnóstico.

## Configuración CORS global

Para permitir solicitudes desde cualquier origen (CORS), el proyecto implementa un filtro personalizado en `com.surrender.configuration.CORS`. Este filtro:
- Permite todos los orígenes (`Access-Control-Allow-Origin: *`).
- Permite los métodos HTTP estándar (`DELETE, GET, OPTIONS, PATCH, POST, PUT`).
- Permite headers comunes y de autenticación.
- Responde automáticamente a las solicitudes `OPTIONS` con estado 200.
- Se ejecuta con la máxima prioridad gracias a `@Order(Ordered.HIGHEST_PRECEDENCE)`.

**Ubicación:**
- `src/main/java/com/surrender/configuration/CORS.java`

**Motivación:**
- Centraliza la configuración CORS fuera de los controladores y la seguridad, manteniendo la arquitectura limpia y desacoplada.
- Facilita el mantenimiento y futuras modificaciones de políticas CORS.

**Ejemplo de implementación:**
```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORS implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "DELETE, GET, OPTIONS, PATCH, POST, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
    // ...existing code...
}
```
