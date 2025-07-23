# 📋 Plan de Migración de Spring Boot (Monolito) a Quarkus (Monolito_v2)

**Versión:** 1.4  
**Última actualización:** 2025-07-23 13:30:00  
**Estado:** Fase 1 ✅ COMPLETADA | Fase 2 ✅ COMPLETADA | Fase 3 � PRÓXIMA

## Estado de las Fases

### ✅ FASE 1: COMPLETADA (2025-07-23 12:15:00)
- [x] Configuración base de Quarkus
- [x] Migración de dependencias principales  
- [x] Configuración de properties y variables de entorno

### ✅ FASE 2: COMPLETADA (2025-07-23 13:30:00)
**Migración de Entidades**
- [x] Conversión de entidades Spring Data JPA a Panache
- [x] Implementación de patrón Active Record
- [x] Corrección de compatibilidad con base de datos existente
- [x] Cambio de tipos Long → Integer para coincidir con Spring Boot
- [x] Ajuste de campos para mantener compatibilidad 100%
- [x] Validación de compilación exitosa
- [x] Configuración de generación de esquema de BD
- [x] **CORRECCIÓN CRÍTICA**: Configuración de Physical Naming Strategy
- [x] **AJUSTE DE NOMBRES**: Tablas y campos ajustados con PhysicalNamingStrategyStandardImpl
- [x] **VALIDACIÓN FINAL**: 9 entidades migradas y compilando correctamente

**Entidades migradas exitosamente:**
- ✅ **Usuario** (tbl_usuario) - Sin anotaciones de seguridad JPA temporalmente
- ✅ **Casa** (tbl_casa) - Con relaciones ManyToMany a Usuario  
- ✅ **Token** (tbl_token) - Para autenticación JWT
- ✅ **UnidadHabitacional** (tbl_unidad_habitacional) - Relaciones con Casa e Inquilino
- ✅ **Inquilino** (tbl_inquilino) - Con campo nombreCompleto
- ✅ **Contrato** (tbl_contrato) - Con float y boolean activo
- ✅ **Recibo** (tbl_recibo) - Con YearMonth y relaciones
- ✅ **DetalleRecibo** (tbl_detalle_recibo) - Detalle de facturación
- ✅ **TarifaServicio** (tbl_tarifa_servicio) - Tarifas con rangos y relación a Casa

**Configuración de compatibilidad:**
- ✅ Physical Naming Strategy configurada: `PhysicalNamingStrategyStandardImpl`
- ✅ Hibernate configurado para `update` (sin crear nuevas tablas)
- ✅ Tipos de datos idénticos a Spring Boot (Integer IDs, float, boolean)
- ✅ Relaciones exactas mantenidas
- ✅ Campos específicos preservados (nombreCompleto, activo, etc.)

### � FASE 3: PRÓXIMA
**Migración de Repositorios**
- [ ] Conversión de interfaces Spring Data JPA a Panache Repository
- [ ] Migración de métodos de consulta personalizados  
- [ ] Implementación de patrón Repository o Active Record
- [ ] Validación de funcionalidad de repositorios

### 📝 FASE 4: PENDIENTE - CONFIGURACIÓN AVANZADA DE SEGURIDAD
**Habilitación de Quarkus Security JPA**
- [ ] **TEMPORAL**: Quarkus Security JPA está deshabilitado para mantener compatibilidad con BD existente
- [ ] Evaluar agregar campo `roles` a tabla `tblUsuario` 
- [ ] Implementar migración de datos para roles existentes
- [ ] Habilitar nuevamente `quarkus-security-jpa` en pom.xml
- [ ] Restaurar anotaciones `@UserDefinition`, `@Username`, `@Password`, `@Roles`
- [ ] Configurar roles por defecto para usuarios existentes

**Nota**: Este paso se realizará después de completar la migración básica para evitar alteraciones en la base de datos durante la coexistencia.

## 🔍 Análisis del Estado Actual

### **Proyecto Monolito (Spring Boot)**
- **Framework:** Spring Boot 3.3.5
- **Java:** 21
- **Base de datos:** MySQL
- **Seguridad:** Spring Security + JWT
- **Dependencias principales:**
  - Spring Web, JPA, Security, HATEOAS, Actuator
  - JWT (jjwt 0.12.3)
  - MySQL Connector

### **Entidades identificadas:**
- `Usuario` (UserDetails) - Gestión de usuarios
- `Casa` - Propiedades inmobiliarias  
- `UnidadHabitacional` - Unidades dentro de casas
- `Inquilino` - Personas que alquilan
- `Contrato` - Contratos de alquiler
- `Recibo` y `DetalleRecibo` - Facturación
- `TarifaServicio` - Tarifas de servicios
- `Token` - Gestión de JWT

### **Arquitectura actual:**
- Controladores REST con `@RestController`
- Servicios con interfaces y implementaciones
- Repositorios con Spring Data JPA
- Configuración de seguridad con filtros JWT
- DTOs para respuestas API

---

## 🎯 Plan de Migración por Fases

### **FASE 1: Configuración Base de Quarkus** ✅ **COMPLETADA**

**Estado:** ✅ **COMPLETADA EXITOSAMENTE**

**Tareas completadas:**
1. ✅ **Actualizado `pom.xml`** - Agregadas todas las extensiones necesarias:
   ```xml
   <!-- Seguridad -->
   <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-security</artifactId>
   </dependency>
   <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-security-jpa</artifactId>
   </dependency>
   <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-smallrye-jwt</artifactId>
   </dependency>
   
   <!-- Validación -->
   <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-hibernate-validator</artifactId>
   </dependency>
   
   <!-- OpenAPI/Swagger -->
   <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-smallrye-openapi</artifactId>
   </dependency>
   ```

2. ✅ **Configurado `application.properties`** - Migrada configuración completa:
   ```properties
   # Base de datos (variables de entorno)
   quarkus.datasource.db-kind=mysql
   quarkus.datasource.username=${USER_BD:root}
   quarkus.datasource.password=${PASSWORD_BD:root}
   quarkus.datasource.jdbc.url=jdbc:mysql://${HOST_BD:localhost}:${PORT_BD:3306}/${NAME_BD:renthome}
   
   # Hibernate
   quarkus.hibernate-orm.database.generation=update
   quarkus.hibernate-orm.log.sql=false
   
   # JWT
   mp.jwt.verify.publickey.location=${JWT_SECRET_KEY}
   mp.jwt.verify.issuer=renthome
   smallrye.jwt.sign.key.location=${JWT_SECRET_KEY}
   
   # CORS
   quarkus.http.cors=true
   quarkus.http.cors.origins=*
   ```

3. ✅ **Creado archivo `.env`** - Variables de entorno configuradas
4. ✅ **Limpieza de archivos** - Eliminados ejemplos no necesarios
5. ✅ **Validación exitosa** - Proyecto compila y ejecuta correctamente
6. ✅ **Quarkus en modo desarrollo** - Servidor funcionando en http://localhost:8080

**Resultado:** Base de Quarkus lista para migración de entidades

### **FASE 2: Migración de Entidades** 🔄

**Conversión Spring JPA → Quarkus Hibernate ORM**

**Cambios principales:**
- `@Entity` se mantiene igual
- Cambiar `org.springframework` por `jakarta.persistence`
- Implementar Panache Entity pattern (opcional pero recomendado)

**Entidades a migrar:**
1. **Usuario** (Prioridad Alta - Base para autenticación)
2. **Token** (Prioridad Alta - Para JWT)
3. **Casa** (Prioridad Media)
4. **UnidadHabitacional** (Prioridad Media)
5. **Inquilino** (Prioridad Media)
6. **Contrato** (Prioridad Media)
7. **Recibo, DetalleRecibo, TarifaServicio** (Prioridad Baja)

**Ejemplo de migración Usuario:**
```java
// De Spring Boot UserDetails a Quarkus Security Identity
@Entity
@Table(name = "tblUsuario")
public class Usuario extends PanacheEntity {
    
    @Column(nullable = false)
    public String nombre;
    
    @Column(nullable = false)
    public String email;
    
    @Column(nullable = false)
    public String password;
    
    @Column(nullable = false)
    public boolean activo;
    
    @ManyToMany
    public List<Casa> casas;
    
    // Métodos Panache
    public static Usuario findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
```

### **FASE 3: Migración de Repositorios** 🔄

**Conversión Spring Data JPA → Panache Repository**

**Estrategias:**
1. **Opción A:** Usar Panache Entity (Active Record pattern)
2. **Opción B:** Usar Panache Repository pattern 

**Ejemplo conversión:**
```java
// Spring Boot Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}

// Quarkus Panache Repository
@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    
    public Optional<Usuario> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    public List<Usuario> findActivUsers() {
        return list("activo", true);
    }
}
```

### **FASE 4: Migración de Servicios** 🔄

**Conversión Spring Services → CDI Services**

**Cambios principales:**
- `@Service` → `@ApplicationScoped`
- `@Autowired` → `@Inject`
- Mantener interfaces (buena práctica)

**Servicios a migrar:**
1. **UsuarioService** y **AuthenticationService** (Prioridad Alta)
2. **JwtService** y **TokenService** (Prioridad Alta)  
3. **InquilinoService** (Prioridad Media)
4. **CRUDService** (Prioridad Media)

**Ejemplo migración:**
```java
// Spring Boot Service
@Service
public class UsuarioServiceImpl implements IUsuarioService {
    @Autowired
    private UsuarioRepo usuarioRepo;
}

// Quarkus Service
@ApplicationScoped
public class UsuarioServiceImpl implements IUsuarioService {
    @Inject
    UsuarioRepository usuarioRepository;
}
```

### **FASE 5: Migración de Seguridad** 🔒

**Conversión Spring Security → Quarkus Security**

**Componentes críticos:**
1. **JWT Configuration**
2. **Authentication Filter** 
3. **User Identity Provider**
4. **Security Config**

**Implementación Quarkus:**
```java
// JWT Token Generator
@ApplicationScoped
public class TokenService {
    
    @ConfigProperty(name = "mp.jwt.sign.key.location")
    String privateKeyLocation;
    
    public String generateToken(Usuario usuario) {
        return Jwt.issuer("renthome")
                .upn(usuario.email)
                .groups(Set.of("user"))
                .expiresAt(Instant.now().plusSeconds(43200))
                .sign();
    }
}

// Security Identity Augmentor
@ApplicationScoped
public class RoleAugmentor implements SecurityIdentityAugmentor {
    
    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, 
                                       AuthenticationRequestContext context) {
        // Agregar roles desde BD
        return Uni.createFrom().item(identity);
    }
}
```

### **FASE 6: Migración de Controladores** 🌐

**Conversión Spring MVC → JAX-RS**

**Cambios principales:**
- `@RestController` → `@Path`
- `@RequestMapping` → `@Path`
- `@GetMapping` → `@GET`
- `@PostMapping` → `@POST`
- `@RequestBody` → sin anotación (automático)
- `@PathVariable` → `@PathParam`
- `ResponseEntity` → `Response` o retorno directo

**Ejemplo migración:**
```java
// Spring Boot Controller
@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {
    
    @PostMapping
    public ResponseEntity<APIResponseDTO<Usuario>> registrar(@RequestBody Usuario usuario) {
        // lógica
        return ResponseEntity.status(201).body(response);
    }
}

// Quarkus JAX-RS Resource
@Path("/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    
    @POST
    @RolesAllowed("admin")
    public Response registrar(Usuario usuario) {
        // lógica
        return Response.status(201).entity(response).build();
    }
}
```

### **FASE 7: Migración de DTOs y Utilitarios** 📦

**Mantener estructura existente:**
- `APIResponseDTO` - Mantener para consistencia
- DTOs de request/response
- Utilidades y helpers

### **FASE 8: Configuración y Testing** 🧪

**Migrar configuración:**
1. **Variables de entorno** (.env)
2. **CORS configuration**
3. **Logging configuration** 
4. **Database initialization** (import.sql)

**Testing:**
- `@QuarkusTest` en lugar de `@SpringBootTest`
- RestAssured para tests de integración
- Testcontainers para tests con BD

---

## 📅 Cronograma Sugerido

| Fase | Duración | Dependencias | Estado |
|------|----------|--------------|---------|
| **Fase 1** | ✅ Completada | - | ✅ **DONE** |
| **Fase 2** | 2-3 días | Fase 1 | 🔄 **EN PROGRESO** |
| **Fase 3** | 1-2 días | Fase 2 | ⏳ Pendiente |
| **Fase 4** | 2-3 días | Fase 3 | ⏳ Pendiente |
| **Fase 5** | 3-4 días | Fases 2,3,4 | ⏳ Pendiente |
| **Fase 6** | 2-3 días | Fases 4,5 | ⏳ Pendiente |
| **Fase 7** | 1 día | Fase 6 | ⏳ Pendiente |
| **Fase 8** | 2-3 días | Todas | ⏳ Pendiente |

**Total estimado: 2-3 semanas**

---

## ⚠️ Puntos Críticos y Consideraciones

### **Diferencias clave Spring Boot vs Quarkus:**

| Aspecto | Spring Boot | Quarkus |
|---------|-------------|---------|
| **Inyección** | `@Autowired` | `@Inject` (CDI) |
| **REST** | Spring MVC | JAX-RS |
| **ORM** | Spring Data JPA | Hibernate ORM + Panache |
| **Seguridad** | Spring Security | MP-JWT + RBAC |
| **Configuración** | `@Value` | `@ConfigProperty` |
| **Transacciones** | `@Transactional` | `@Transactional` (igual) |

### **Beneficios esperados:**
- ⚡ **Startup time:** De ~3-5s a ~0.3-1s
- 🔋 **Memory usage:** Reducción del 60-70%
- 🚀 **Native compilation:** Posibilidad de GraalVM native
- 🔧 **Dev experience:** Hot reload mejorado
- ☁️ **Cloud native:** Optimizado para containers

### **Riesgos y mitigaciones:**
- **Ecosistema más limitado:** Verificar disponibilidad de extensiones
- **Curva de aprendizaje:** Capacitación en CDI/JAX-RS
- **Debugging:** Logs detallados durante migración
- **Testing exhaustivo:** Tests de regresión completos

---

## 🚀 Próximos Pasos

1. ✅ **Completar Fase 1:** ~~Actualizar dependencias en `pom.xml`~~ **COMPLETADO**
2. 🎯 **Iniciar Fase 2:** Comenzar con entidad `Usuario` **← SIGUIENTE**
3. **Validar cada fase:** Tests unitarios y de integración
4. **Documentar cambios:** Mantener registro de modificaciones

### **Estado actual del proyecto:**
- 🟢 **Quarkus Base:** Configurado y funcionando
- 🟢 **Dependencias:** Todas las extensiones instaladas
- 🟢 **Configuración:** Variables y propiedades OK
- 🟢 **Compilación:** Sin errores
- 🟢 **Servidor:** Ejecutándose en modo desarrollo
- 🔄 **Base de datos:** Configurada (requiere creación de BD)

---

## 📚 Referencias y Recursos

### **Documentación oficial:**
- [Quarkus Migration Guide](https://quarkus.io/guides/migration-guide)
- [Quarkus Security Guide](https://quarkus.io/guides/security)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)

### **Guías específicas:**
- [Spring Boot to Quarkus Migration](https://quarkus.io/guides/spring-boot-properties)
- [JAX-RS vs Spring MVC](https://quarkus.io/guides/rest-json)
- [CDI vs Spring DI](https://quarkus.io/guides/cdi)

### **Herramientas útiles:**
- [Quarkus CLI](https://quarkus.io/guides/cli-tooling)
- [Quarkus Dev Services](https://quarkus.io/guides/dev-services)
- [Quarkus Testing](https://quarkus.io/guides/getting-started-testing)

---

**Autor:** GitHub Copilot  
**Fecha:** Julio 23, 2025  
**Versión:** 1.1 - Fase 1 Completada  
**Proyecto:** RentHome Backend Migration

### **Log de Cambios:**
- **v1.1 (23/07/2025):** ✅ Fase 1 completada exitosamente
- **v1.0 (23/07/2025):** Plan inicial creado
