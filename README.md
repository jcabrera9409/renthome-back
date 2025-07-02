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

## DTO de respuesta unificada
- Se creó el DTO genérico `APIResponseDTO<T>` en el paquete `dto`, que será la única forma de respuesta para todos los controllers.
- Sus campos son:
  - `boolean success`: Indica si la operación fue exitosa.
  - `String message`: Mensaje descriptivo de la respuesta.
  - `T data`: Datos devueltos por la operación.
  - `int statusCode`: Código de estado HTTP asociado.
  - `LocalDateTime timestamp`: Fecha y hora de la respuesta.

## Controladores REST
- Los controladores `UsuarioController` e `InquilinoController` se encuentran en el paquete `controller`.
- Todos los endpoints están bajo el prefijo `/v1` (por ejemplo, `/v1/usuarios`, `/v1/inquilinos`).
- Todas las respuestas utilizan el DTO `APIResponseDTO` para unificar la estructura de respuesta.
- Los controladores implementan operaciones CRUD completas para sus respectivas entidades.
- Ambos controladores exponen un endpoint `/filtrar` que permite obtener resultados paginados usando parámetros de Spring Data (`Pageable`).
- La gestión de tokens se realiza a través del controlador de usuario, no existe un controlador dedicado para tokens.

## Configuración
- **application.properties**: Configuración de la aplicación, incluyendo conexión a la base de datos mediante variables de entorno.
- **.env**: Variables de entorno para la conexión a MySQL (host, puerto, nombre de la base, usuario y contraseña).

## Dependencias principales
- `spring-boot-starter-web`: Para exponer servicios REST.
- `spring-boot-starter-data-jpa`: Para persistencia de datos.
- `mysql-connector-j`: Driver para MySQL.
- `spring-boot-starter-hateoas`: Para hipermedios en las respuestas REST.
- `spring-boot-starter-test`: Para pruebas.

## Ejecución
1. Configura las variables de entorno en el archivo `.env`.
2. Ejecuta el proyecto con Maven Wrapper:
   ```sh
   ./mvnw spring-boot:run
   ```

## Notas
- El proyecto está preparado para usarse con Java 21.
- El código fuente principal se encuentra en `src/main/java/com/surrender/`.
- Los modelos de datos están en `src/main/java/com/surrender/model/`.
- Los repositorios se encuentran en `src/main/java/com/surrender/repo/` y siguen una estructura genérica para facilitar la escalabilidad.
- Los servicios y sus implementaciones están en `src/main/java/com/surrender/service/` y `src/main/java/com/surrender/service/impl/` respectivamente, siguiendo una arquitectura limpia y reutilizable.
- El DTO `APIResponseDTO` en `src/main/java/com/surrender/dto/` centraliza la respuesta de todos los endpoints.
- Los controladores REST están en `src/main/java/com/surrender/controller/` y exponen endpoints CRUD y de paginación bajo el prefijo `/v1`.
- La lógica CRUD genérica y la abstracción de repositorios permiten un desarrollo más rápido y menos repetitivo para nuevas entidades.
- La gestión de tokens JWT está centralizada y asociada a la entidad usuario.

---
Generado automáticamente a partir del análisis del código fuente y archivos de configuración.
