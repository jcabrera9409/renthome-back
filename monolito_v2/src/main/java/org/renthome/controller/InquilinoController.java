package org.renthome.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.renthome.model.Inquilino;
import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.IInquilinoService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Inquilinos con autenticación JWT
 */
@Path("/api/inquilinos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InquilinoController {

    private static final Logger LOG = Logger.getLogger(InquilinoController.class);

    @Inject
    IInquilinoService inquilinoService;

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
            
            Usuario usuario = usuarioRepository.find("id", userId).firstResult();
            LOG.infof("usuarioRepository.find('id', %d) retornó: %s", userId, usuario != null ? "Usuario encontrado" : "null");
            
            return usuario;
        } catch (Exception e) {
            LOG.errorf(e, "Error en getCurrentUser()");
            return null;
        }
    }

    /**
     * Listar todos los inquilinos del usuario autenticado
     */
    @GET
    @RolesAllowed("user")
    public Response listar() {
        LOG.info("Iniciando listado de inquilinos");
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                LOG.error("getCurrentUser() retornó null");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Listando inquilinos para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // Obtener todos los inquilinos y filtrar por los relacionados con las casas del usuario
            List<Inquilino> todosLosInquilinos = inquilinoService.listarTodos();
            List<Inquilino> inquilinos = todosLosInquilinos.stream()
                    .filter(inquilino -> inquilino.contratos != null &&
                           inquilino.contratos.stream().anyMatch(contrato -> 
                               contrato.unidad != null && 
                               contrato.unidad.casa != null &&
                               currentUser.casas.stream().anyMatch(casa -> 
                                   casa.id.equals(contrato.unidad.casa.id))))
                    .toList();
            LOG.infof("Número de inquilinos encontrados: %d", inquilinos != null ? inquilinos.size() : 0);
            
            return Response.ok(inquilinos).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error listando inquilinos");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Obtener un inquilino específico por ID (solo si está relacionado con las casas del usuario autenticado)
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
            
            LOG.infof("Obteniendo inquilino ID: %d para usuario: %s", id, currentUser.email);
            
            Optional<Inquilino> inquilinoOpt = inquilinoService.listarPorId(id);
            if (inquilinoOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Inquilino no encontrado\"}")
                        .build();
            }
            
            Inquilino inquilino = inquilinoOpt.get();
            // Verificar que el inquilino está relacionado con las casas del usuario autenticado
            boolean tieneAcceso = inquilino.contratos != null &&
                                inquilino.contratos.stream()
                                    .anyMatch(contrato -> contrato.unidad != null && 
                                             contrato.unidad.casa != null &&
                                             contrato.unidad.casa.usuarios.stream()
                                                     .anyMatch(u -> u.id.equals(currentUser.id)));
            
            if (!tieneAcceso) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para acceder a este inquilino\"}")
                        .build();
            }
            
            return Response.ok(inquilino).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error obteniendo inquilino ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Crear un nuevo inquilino para el usuario autenticado
     */
    @POST
    @RolesAllowed("user")
    public Response crear(@Valid Inquilino inquilino) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Creando nuevo inquilino para usuario: %s", currentUser.email);
            
            Inquilino inquilinoCreado = inquilinoService.registrar(inquilino);
            return Response.status(Response.Status.CREATED).entity(inquilinoCreado).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error creando inquilino");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Actualizar un inquilino existente (solo si está relacionado con las casas del usuario autenticado)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("user")
    public Response actualizar(@PathParam("id") Integer id, @Valid Inquilino inquilino) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Actualizando inquilino ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que el inquilino existe y tiene acceso
            Optional<Inquilino> inquilinoExistenteOpt = inquilinoService.listarPorId(id);
            if (inquilinoExistenteOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Inquilino no encontrado\"}")
                        .build();
            }
            
            Inquilino inquilinoExistente = inquilinoExistenteOpt.get();
            boolean tieneAcceso = inquilinoExistente.contratos != null &&
                                inquilinoExistente.contratos.stream()
                                    .anyMatch(contrato -> contrato.unidad != null && 
                                             contrato.unidad.casa != null &&
                                             contrato.unidad.casa.usuarios.stream()
                                                     .anyMatch(u -> u.id.equals(currentUser.id)));
            
            if (!tieneAcceso) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para modificar este inquilino\"}")
                        .build();
            }
            
            // Establecer el ID antes de actualizar
            inquilino.id = id;
            
            Inquilino inquilinoActualizado = inquilinoService.modificar(inquilino);
            return Response.ok(inquilinoActualizado).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error actualizando inquilino ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Eliminar un inquilino (solo si está relacionado con las casas del usuario autenticado)
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
            
            LOG.infof("Eliminando inquilino ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que el inquilino existe y tiene acceso
            Optional<Inquilino> inquilinoOpt = inquilinoService.listarPorId(id);
            if (inquilinoOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Inquilino no encontrado\"}")
                        .build();
            }
            
            Inquilino inquilino = inquilinoOpt.get();
            boolean tieneAcceso = inquilino.contratos != null &&
                                inquilino.contratos.stream()
                                    .anyMatch(contrato -> contrato.unidad != null && 
                                             contrato.unidad.casa != null &&
                                             contrato.unidad.casa.usuarios.stream()
                                                     .anyMatch(u -> u.id.equals(currentUser.id)));
            
            if (!tieneAcceso) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para eliminar este inquilino\"}")
                        .build();
            }
            
            inquilinoService.eliminar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.errorf(e, "Error eliminando inquilino ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint público para obtener información general sobre inquilinos
     */
    @GET
    @Path("/info")
    public Response info() {
        return Response.ok("{\"message\": \"Información pública de inquilinos disponible\"}")
                .build();
    }
}
