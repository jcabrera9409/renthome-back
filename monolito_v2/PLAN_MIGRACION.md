# 📋 Plan de Migración de Spring Boot (Monolito) → Quarkus

**Estado:** Fase 1 ✅ COMPLETADA | Fase 2 ✅ COMPLETADA | Fase 3 ✅ COMPLETADA | Fase 4 ✅ COMPLETADA | Fase 5 📝 PRÓXIMA

**Actualizado:** 2025-07-23 16:45:00  
**Versión:** 1.8

## 📋 Resumen de Fases Actualizadas

| Fase | Objetivo | Estado | Descripción |
|------|----------|--------|-------------|
| **Fase 1** | ✅ Configuración Base | **COMPLETADA** | Setup Quarkus, dependencias, properties |
| **Fase 2** | ✅ Entidades | **COMPLETADA** | Migración PanacheEntityBase + Integer ID |
| **Fase 3** | ✅ Repositorios | **COMPLETADA** | Patrón Repository con PanacheRepository |
| **Fase 4** | ✅ Servicios Básicos | **COMPLETADA** | CDI Services (sin seguridad) |
| **Fase 5** | 📝 Seguridad JWT | **PRÓXIMA** | JWT Básico (sin roles) |
| **Fase 6** | 📝 Mejoras Futuras | Opcional | Roles avanzados (futuro) |
| **Fase 7** | 📝 Controladores | Pendiente | Spring MVC → JAX-RS |
| **Fase 8** | 📝 Testing | Pendiente | Pruebas y validación final |

---

## Estado de las Fases

### ✅ FASE 1: COMPLETADA (2025-07-23 12:15:00)
**Configuración Base de Quarkus**
- [x] Configuración base de Quarkus
- [x] Migración de dependencias principales  
- [x] Configuración de properties y variables de entorno
- [x] Setup de estructura de proyecto

**Resultado:** Proyecto Quarkus funcionando con dependencias básicas

### ✅ FASE 2: COMPLETADA (2025-07-23 13:30:00)
**Migración de Entidades**
- [x] **COMPATIBILIDAD BD**: PanacheEntityBase con Integer ID para mantener esquema existente
- [x] **9 ENTIDADES MIGRADAS**: Usuario, Casa, Token, UnidadHabitacional, Inquilino, Contrato, Recibo, DetalleRecibo, TarifaServicio
- [x] **NAMING STRATEGY**: PhysicalNamingStrategyStandardImpl configurado
- [x] **VALIDACIÓN**: Todas las entidades mapean correctamente a tablas existentes
- [x] **JPA ANNOTATIONS**: @Entity, @Table, @Column, @JoinColumn mantenidas

**Resultado:** Entidades 100% compatibles con base de datos Spring Boot existente

### ✅ FASE 3: COMPLETADA (2025-07-23 16:00:00)
**Migración de Repositorios**
- [x] **REFACTORIZACIÓN**: Métodos de consulta movidos desde entidades a repositorios dedicados
- [x] **PATRÓN REPOSITORY**: PanacheRepository implementado manteniendo separación de responsabilidades
- [x] Conversión de interfaces Spring Data JPA a Panache Repository
- [x] Migración de métodos de consulta personalizados desde Spring Boot
- [x] **10 REPOSITORIOS CREADOS**: `UsuarioRepository`, `CasaRepository`, `TokenRepository`, `InquilinoRepository`, `ContratoRepository`, `ReciboRepository`, `TarifaServicioRepository`, `UnidadHabitacionalRepository`, `DetalleReciboRepository`
- [x] **LIMPIEZA COMPLETA**: Todos los métodos estáticos removidos de las entidades
- [x] **COMPATIBILIDAD**: `PanacheEntityBase` con `Integer id` mantenido para Spring Boot
- [x] Validación de funcionalidad y compilación exitosa

**Resultado:** Separación perfecta entre entidades (mapeo) y repositorios (consultas)

### ✅ FASE 4: COMPLETADA (2025-07-23 16:20:00)
**Conversión de Spring Services → CDI Services**
- [x] **PATRÓN CDI**: Migrar anotaciones `@Service` → `@ApplicationScoped` y `@Autowired` → `@Inject`
- [x] **PRIORIDAD ALTA**: 
  - [x] `UsuarioService` (sin lógica de autenticación)
  - [x] Servicios CRUD básicos
- [x] **PRIORIDAD MEDIA**:
  - [x] `InquilinoService` 
  - [x] `CasaService`
  - [x] `ContratoService`
- [x] **VALIDACIÓN**: Compilación exitosa y funcionamiento básico
- [x] **TRANSACCIONES**: Implementar `@Transactional` en métodos de escritura
- [x] **ESTRUCTURA CREADA**:
  - [x] `ICRUD<T, ID>` - Interface base genérica
  - [x] `CRUDImpl<T, ID>` - Implementación base con Panache
  - [x] `IUsuarioService` + `UsuarioServiceImpl` - Con registrarUsuario() sin encriptación (temporal)
  - [x] `IInquilinoService` + `InquilinoServiceImpl` - CRUD básico
  - [x] `ICasaService` + `CasaServiceImpl` - CRUD básico  
  - [x] `IContratoService` + `ContratoServiceImpl` - CRUD básico

**Resultado:** Capa de servicios completamente migrada sin dependencias de Spring

**Nota:** Encriptación de contraseñas se implementará en Fase 5 junto con la seguridad

### 📝 FASE 5: PENDIENTE - MIGRACIÓN DE SEGURIDAD JWT BÁSICA
**Conversión Spring Security → Quarkus Security (SIN Sistema de Roles)**
- [ ] **JWT CONFIGURATION**: Migrar configuración de tokens JWT usando SmallRye JWT
- [ ] **AUTHENTICATION SERVICE**: Migrar lógica de login/logout básica
- [ ] **JWT SERVICE**: Generación y validación de tokens JWT
- [ ] **TOKEN SERVICE**: Gestión de tokens en base de datos (invalidación)
- [ ] **SECURITY CONFIG**: Configurar rutas públicas vs privadas en `application.properties`
- [ ] **ENCRIPTACIÓN**: Implementar BCrypt para contraseñas (pendiente desde Fase 4)
- [ ] **FILTRO JWT**: Configuración básica de validación de tokens sin roles
- [ ] **VALIDACIÓN**: Pruebas de autenticación básica (login/logout)

**Enfoque Simplificado:**
- ✅ **Solo autenticación** - Usuario logueado vs no logueado
- ✅ **JWT como único mecanismo** - Sin interfaces UserDetails
- ✅ **Rutas públicas/privadas** - Sin granularidad de roles
- ✅ **Compatibilidad 100%** - Mismo comportamiento que Spring Boot actual

**Nota**: Sistema mucho más simple, sin roles ni autorización compleja

### 📝 FASE 6: OPCIONAL - MEJORAS DE SEGURIDAD (FUTURO)
**Posibles mejoras futuras (no necesarias para la migración)**
- [ ] **ROLES AVANZADOS**: Si en el futuro se requiere sistema de roles granular
- [ ] **AUTORIZACIÓN COMPLEJA**: @RolesAllowed con múltiples roles
- [ ] **SEGURIDAD JPA**: Quarkus Security JPA si se agregan campos de roles
- [ ] **OAUTH2/OIDC**: Integración con proveedores externos

**Nota**: Esta fase es completamente opcional y solo se implementaría si hay requerimientos futuros

### 📝 FASE 7: PENDIENTE - MIGRACIÓN DE CONTROLADORES
**Conversión Spring MVC → JAX-RS**
- [ ] **ANOTACIONES**: `@RestController` → `@Path`, `@GetMapping` → `@GET`
- [ ] **PARÁMETROS**: `@RequestBody` → automático, `@PathVariable` → `@PathParam`
- [ ] **RESPUESTAS**: `ResponseEntity` → `Response` o retorno directo
- [ ] **SEGURIDAD BÁSICA**: Rutas públicas vs privadas (sin @RolesAllowed)
- [ ] **VALIDACIÓN**: Testing de todos los endpoints

### 📝 FASE 8: PENDIENTE - TESTING Y VALIDACIÓN FINAL
**Pruebas Completas**
- [ ] Testing unitario de servicios
- [ ] Testing de integración con base de datos
- [ ] Pruebas de seguridad y autenticación
- [ ] Validación de rendimiento
- [ ] Documentación actualizada

---

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

---

## 🚀 Implementación Detallada por Fases

### **FASE 4: Migración de Servicios** 🔄

**Conversión Spring Services → CDI Services (SIN Seguridad)**

**Cambios principales:**
- `@Service` → `@ApplicationScoped`
- `@Autowired` → `@Inject`
- Mantener interfaces (buena práctica)
- Implementar `@Transactional` para operaciones de escritura

**Servicios de Negocio a migrar (SIN lógica de autenticación):**
1. **PRIORIDAD ALTA:**
   - `UsuarioService` (operaciones CRUD básicas, sin login/logout)
   - Servicios CRUD genéricos
2. **PRIORIDAD MEDIA:**
   - `InquilinoService` → Gestión de inquilinos
   - `CasaService` → Gestión de propiedades  
   - `ContratoService` → Gestión de contratos
   - `ReciboService` → Gestión de recibos y facturación

**Ejemplo migración:**
```java
// Spring Boot Service
@Service
public class UsuarioServiceImpl implements IUsuarioService {
    @Autowired
    private UsuarioRepo usuarioRepo;
    
    public Usuario save(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }
}

// Quarkus Service
@ApplicationScoped
public class UsuarioServiceImpl implements IUsuarioService {
    @Inject
    UsuarioRepository usuarioRepository;
    
    @Transactional
    public Usuario save(Usuario usuario) {
        usuarioRepository.persist(usuario);
        return usuario;
    }
}
```

### **FASE 5: Migración de Seguridad JWT Básica** 🔒

**Conversión Spring Security → Quarkus Security (SIN Roles)**

**Servicios de Seguridad a migrar:**
1. **AuthenticationService** → Lógica de login/logout básica
2. **JwtService** → Generación y validación de tokens
3. **TokenService** → Gestión de tokens en base de datos

**Componentes Spring Security → Quarkus Security:**

| **Spring Security** | **Quarkus Security** | **Descripción** |
|---------------------|---------------------|-----------------|
| `@EnableWebSecurity` | `application.properties` | Configuración de rutas |
| `JwtRequestFilter` | SmallRye JWT automático | Validación JWT |
| `UserDetailsService` | Eliminado | No necesario sin roles |
| `SecurityContextHolder` | `SecurityIdentity` | Contexto de usuario |
| Rutas permitAll/authenticated | `quarkus.http.auth.permission.*` | Control de acceso |

**Implementación Quarkus Simplificada:**
```java
// 1. AuthenticationService - Solo login/logout
@ApplicationScoped
public class AuthenticationServiceImpl implements IAuthenticationService {
    @Inject
    UsuarioRepository usuarioRepository;
    
    @Inject
    JwtService jwtService;
    
    public LoginResponse login(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.activo && BCrypt.checkpw(password, usuario.password)) {
                String token = jwtService.generateToken(usuario);
                return new LoginResponse(token, usuario);
            }
        }
        throw new AuthenticationException("Credenciales inválidas");
    }
}

// 2. JWT Service - Sin roles, solo autenticación
@ApplicationScoped
public class JwtServiceImpl implements IJwtService {
    
    @ConfigProperty(name = "smallrye.jwt.sign.key")
    String secretKey;
    
    public String generateToken(Usuario usuario) {
        return Jwt.issuer("renthome")
                .upn(usuario.email)                    // Email como principal
                .groups(Set.of("authenticated"))       // Un solo grupo básico
                .expiresAt(Instant.now().plusSeconds(43200))
                .sign();
    }
    
    public boolean validateToken(String token) {
        try {
            // SmallRye JWT lo valida automáticamente
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// 3. Configuración de Seguridad en application.properties
# Rutas públicas
quarkus.http.auth.permission.permit1.paths=/v1/auth/login,/v1/usuarios
quarkus.http.auth.permission.permit1.policy=permit

# Rutas privadas - solo requieren JWT válido
quarkus.http.auth.permission.deny1.paths=/*
quarkus.http.auth.permission.deny1.policy=authenticated

# JWT Configuration
smallrye.jwt.sign.key=${JWT_SECRET_KEY}
mp.jwt.verify.publickey=${JWT_SECRET_KEY}
mp.jwt.verify.issuer=renthome
```

**Ventajas del enfoque simplificado:**
- ✅ **Sin complejidad de roles** - Solo usuario autenticado vs anónimo
- ✅ **Migración directa** - Mismo comportamiento que Spring Boot actual
- ✅ **SmallRye JWT automático** - Sin necesidad de filtros personalizados
- ✅ **Configuración declarativa** - Todo en application.properties
```

### **FASE 7: Migración de Controladores** 🌐

**Conversión Spring MVC → JAX-RS (Sin Roles)**

**Cambios principales:**
- `@RestController` → `@Path`
- `@RequestMapping` → `@Path`
- `@GetMapping` → `@GET`
- `@PostMapping` → `@POST`
- `@RequestBody` → sin anotación (automático)
- `@PathVariable` → `@PathParam`
- `ResponseEntity` → `Response` o retorno directo

**Ejemplo migración sin roles:**
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

// Quarkus JAX-RS Resource - Sin @RolesAllowed
@Path("/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    
    // Ruta pública (configurada en application.properties)
    @POST
    public Response registrar(Usuario usuario) {
        // lógica
        return Response.status(201).entity(response).build();
    }
    
    // Ruta privada (requiere JWT automáticamente)
    @GET
    public List<Usuario> listar() {
        // Solo usuarios autenticados pueden acceder
        return usuarios;
    }
}
```

**Control de acceso por configuración:**
```properties
# En application.properties - No en anotaciones
quarkus.http.auth.permission.public.paths=/v1/auth/login,/v1/usuarios
quarkus.http.auth.permission.public.policy=permit

quarkus.http.auth.permission.private.paths=/*
quarkus.http.auth.permission.private.policy=authenticated
```
```

---

## 📅 Cronograma Sugerido

| Fase | Duración | Dependencias | Estado |
|------|----------|--------------|---------|
| **Fase 1** | ✅ Completada | - | ✅ **COMPLETADA** |
| **Fase 2** | ✅ Completada | Fase 1 | ✅ **COMPLETADA** |
| **Fase 3** | ✅ Completada | Fase 2 | ✅ **COMPLETADA** |
| **Fase 4** | 1-2 días | Fase 3 | ⏳ **PRÓXIMA** |
| **Fase 5** | 1-2 días | Fase 4 | ⏳ Pendiente |
| **Fase 6** | Opcional | - | ⏳ Futuro |
| **Fase 7** | 2-3 días | Fase 5 | ⏳ Pendiente |
| **Fase 8** | 1 día | Fase 7 | ⏳ Pendiente |

**Total estimado: 1-2 semanas restantes**

---

## ⚠️ Puntos Críticos y Consideraciones

### **Diferencias clave Spring Boot vs Quarkus:**

| Aspecto | Spring Boot | Quarkus |
|---------|-------------|---------|
| **Inyección** | `@Autowired` | `@Inject` (CDI) |
| **REST** | Spring MVC | JAX-RS |
| **Servicios** | `@Service` | `@ApplicationScoped` |
| **Transacciones** | `@Transactional` | `@Transactional` |
| **Seguridad** | Spring Security | SmallRye JWT + Security |
| **Configuración** | `application.properties` | `application.properties` |

### **Compatibilidad con Base de Datos:**
- ✅ **Esquema preservado**: No se modifican tablas existentes
- ✅ **IDs compatibles**: `Integer` en lugar de `Long`
- ✅ **Naming strategy**: `PhysicalNamingStrategyStandardImpl`
- ✅ **Relaciones**: Todas las `@JoinColumn` mantenidas

### **Estrategias de Testing:**
- Usar `@QuarkusTest` en lugar de `@SpringBootTest`
- Testcontainers para testing con base de datos
- RestAssured para testing de APIs
- Mocking con CDI y `@Mock`

---

## 🎯 Próximos Pasos

1. **Iniciar Fase 4**: Migración de servicios básicos (sin seguridad)
2. **Validar funcionamiento**: Compilación y testing básico
3. **Continuar Fase 5**: Migración completa de seguridad
4. **Fase 7**: Migración de controladores con seguridad habilitada
5. **Fase 8**: Testing final y documentación

---

**📝 Notas importantes:**
- Mantener coexistencia con Spring Boot durante desarrollo
- No modificar base de datos existente
- Validar cada fase antes de continuar
- Documentar cambios y decisiones técnicas
