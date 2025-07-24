package org.renthome.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.renthome.model.Casa;
import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.ICasaService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Casas con autenticación JWT
 */
@Path("/api/casas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CasaController {

    private static final Logger LOG = Logger.getLogger(CasaController.class);

    @Inject
    ICasaService casaService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioRepository usuarioRepository;

    /**
     * Obtener el ID del usuario autenticado desde el JWT
     */
    private Integer getCurrentUserId() {
        try {
            LOG.info("Intentando obtener userId del JWT");
            
            if (jwt == null) {
                LOG.error("JWT es null - token no se pudo inyectar");
                return null;
            }
            
            LOG.infof("JWT disponible. Claims disponibles: %s", jwt.getClaimNames());
            LOG.infof("JWT subject: %s", jwt.getSubject());
            LOG.infof("JWT upn: %s", jwt.getName());
            
            Object userIdObj = jwt.getClaim("userId");
            LOG.infof("JWT claim 'userId' objeto: %s (tipo: %s)", 
                     userIdObj, 
                     userIdObj != null ? userIdObj.getClass().getSimpleName() : "null");
            
            if (userIdObj == null) {
                LOG.error("Claim 'userId' no encontrado en el JWT");
                return null;
            }
            
            Integer result = null;
            if (userIdObj instanceof Number) {
                result = ((Number) userIdObj).intValue();
            } else if (userIdObj instanceof String) {
                try {
                    result = Integer.parseInt((String) userIdObj);
                } catch (NumberFormatException e) {
                    LOG.errorf("No se pudo convertir userId string a Integer: %s", userIdObj);
                    return null;
                }
            } else {
                // Para JsonLongNumber y otros tipos, intentar convertir a String primero
                try {
                    result = Integer.parseInt(userIdObj.toString());
                } catch (NumberFormatException e) {
                    LOG.errorf("No se pudo convertir userId a Integer: %s (tipo: %s)", 
                              userIdObj, userIdObj.getClass().getSimpleName());
                    return null;
                }
            }
            
            LOG.infof("userId convertido: %s", result);
            return result;
        } catch (Exception e) {
            LOG.errorf(e, "Error al obtener userId del JWT");
            return null;
        }
    }

    /**
     * Obtener el usuario autenticado
     */
    private Usuario getCurrentUser() {
        try {
            Integer userId = getCurrentUserId();
            LOG.infof("getCurrentUserId() retornó: %s", userId);
            
            if (userId == null) {
                LOG.error("userId es null");
                return null;
            }
            
            // Usar find en lugar de findById para evitar problemas de tipos
            Usuario usuario = usuarioRepository.find("id", userId).firstResult();
            LOG.infof("usuarioRepository.find('id', %d) retornó: %s", userId, usuario != null ? "Usuario encontrado" : "null");
            
            return usuario;
        } catch (Exception e) {
            LOG.errorf(e, "Error en getCurrentUser()");
            return null;
        }
    }

    /**
     * Listar todas las casas del usuario autenticado
     */
    @GET
    @RolesAllowed("user")
    public Response listar() {
        LOG.info("Iniciando listado de casas");
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                LOG.error("getCurrentUser() retornó null");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Listando casas para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // Obtener casas del usuario a través de la relación Many-to-Many
            List<Casa> casas = currentUser.casas;
            LOG.infof("Número de casas encontradas: %d", casas != null ? casas.size() : 0);
            
            return Response.ok(casas).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error listando casas");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Obtener una casa específica por ID (solo si pertenece al usuario autenticado)
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Obteniendo casa ID: %d para usuario: %s", id, currentUser.email);
            
            Optional<Casa> casaOpt = casaService.listarPorId(id);
            if (casaOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Casa no encontrada\"}")
                        .build();
            }
            
            Casa casa = casaOpt.get();
            // Verificar que la casa pertenece al usuario autenticado (Many-to-Many)
            boolean perteneceAlUsuario = casa.usuarios.stream()
                    .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para acceder a esta casa\"}")
                        .build();
            }
            
            return Response.ok(casa).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error obteniendo casa ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Crear una nueva casa para el usuario autenticado
     */
    @POST
    @RolesAllowed("user")
    public Response crear(@Valid Casa casa) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Creando nueva casa para usuario: %s", currentUser.email);
            
            // Crear la casa primero
            Casa casaCreada = casaService.registrar(casa);
            
            // Agregar la relación Many-to-Many
            if (currentUser.casas == null) {
                currentUser.casas = new java.util.ArrayList<>();
            }
            currentUser.casas.add(casaCreada);
            currentUser.persist();
            
            return Response.status(Response.Status.CREATED).entity(casaCreada).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error creando casa");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Actualizar una casa existente (solo si pertenece al usuario autenticado)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("user")
    public Response actualizar(@PathParam("id") Integer id, @Valid Casa casa) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Actualizando casa ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que la casa existe y pertenece al usuario
            Optional<Casa> casaExistenteOpt = casaService.listarPorId(id);
            if (casaExistenteOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Casa no encontrada\"}")
                        .build();
            }
            
            Casa casaExistente = casaExistenteOpt.get();
            boolean perteneceAlUsuario = casaExistente.usuarios.stream()
                    .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para modificar esta casa\"}")
                        .build();
            }
            
            // Establecer el ID antes de actualizar
            casa.id = id;
            
            Casa casaActualizada = casaService.modificar(casa);
            return Response.ok(casaActualizada).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error actualizando casa ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Eliminar una casa (solo si pertenece al usuario autenticado)
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("user")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Eliminando casa ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que la casa existe y pertenece al usuario
            Optional<Casa> casaOpt = casaService.listarPorId(id);
            if (casaOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Casa no encontrada\"}")
                        .build();
            }
            
            Casa casa = casaOpt.get();
            boolean perteneceAlUsuario = casa.usuarios.stream()
                    .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para eliminar esta casa\"}")
                        .build();
            }
            
            // Remover la relación Many-to-Many antes de eliminar
            currentUser.casas.removeIf(c -> c.id.equals(id));
            currentUser.persist();
            
            casaService.eliminar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.errorf(e, "Error eliminando casa ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint público para obtener información general sobre casas
     */
    @GET
    @Path("/info")
    public Response info() {
        return Response.ok("{\"message\": \"Información pública de casas disponible\"}")
                .build();
    }
}
