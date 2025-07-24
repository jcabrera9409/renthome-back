package org.renthome.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.renthome.model.Contrato;
import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.IContratoService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Contratos con autenticación JWT
 */
@Path("/api/contratos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContratoController {

    private static final Logger LOG = Logger.getLogger(ContratoController.class);

    @Inject
    IContratoService contratoService;

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
     * Listar todos los contratos del usuario autenticado
     */
    @GET
    @RolesAllowed("user")
    public Response listar() {
        LOG.info("Iniciando listado de contratos");
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                LOG.error("getCurrentUser() retornó null");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Listando contratos para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // Obtener todos los contratos y filtrar por las casas del usuario
            List<Contrato> todosLosContratos = contratoService.listarTodos();
            List<Contrato> contratos = todosLosContratos.stream()
                    .filter(contrato -> contrato.unidad != null && 
                           contrato.unidad.casa != null &&
                           currentUser.casas.stream().anyMatch(casa -> 
                               casa.id.equals(contrato.unidad.casa.id)))
                    .toList();
            LOG.infof("Número de contratos encontrados: %d", contratos != null ? contratos.size() : 0);
            
            return Response.ok(contratos).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error listando contratos");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Obtener un contrato específico por ID (solo si pertenece al usuario autenticado)
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
            
            LOG.infof("Obteniendo contrato ID: %d para usuario: %s", id, currentUser.email);
            
            Optional<Contrato> contratoOpt = contratoService.listarPorId(id);
            if (contratoOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Contrato no encontrado\"}")
                        .build();
            }
            
            Contrato contrato = contratoOpt.get();
            // Verificar que el contrato pertenece a una casa del usuario autenticado
            boolean perteneceAlUsuario = contrato.unidad != null && 
                                       contrato.unidad.casa != null &&
                                       contrato.unidad.casa.usuarios.stream()
                                               .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para acceder a este contrato\"}")
                        .build();
            }
            
            return Response.ok(contrato).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error obteniendo contrato ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Crear un nuevo contrato para el usuario autenticado
     */
    @POST
    @RolesAllowed("user")
    public Response crear(@Valid Contrato contrato) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Creando nuevo contrato para usuario: %s", currentUser.email);
            
            // Verificar que la unidad habitacional del contrato pertenece al usuario autenticado
            if (contrato.unidad == null || contrato.unidad.id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Debe especificar una unidad habitacional válida para el contrato\"}")
                        .build();
            }
            
            // Verificar que la unidad pertenece a una casa del usuario
            boolean unidadPerteneceAlUsuario = currentUser.casas.stream()
                    .anyMatch(casa -> casa.unidades.stream()
                            .anyMatch(u -> u.id.equals(contrato.unidad.id)));
            
            if (!unidadPerteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para crear contratos en esta unidad\"}")
                        .build();
            }
            
            Contrato contratoCreado = contratoService.registrar(contrato);
            return Response.status(Response.Status.CREATED).entity(contratoCreado).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error creando contrato");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Actualizar un contrato existente (solo si pertenece al usuario autenticado)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("user")
    public Response actualizar(@PathParam("id") Integer id, @Valid Contrato contrato) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Actualizando contrato ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que el contrato existe y pertenece al usuario
            Optional<Contrato> contratoExistenteOpt = contratoService.listarPorId(id);
            if (contratoExistenteOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Contrato no encontrado\"}")
                        .build();
            }
            
            Contrato contratoExistente = contratoExistenteOpt.get();
            boolean perteneceAlUsuario = contratoExistente.unidad != null && 
                                       contratoExistente.unidad.casa != null &&
                                       contratoExistente.unidad.casa.usuarios.stream()
                                               .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para modificar este contrato\"}")
                        .build();
            }
            
            // Establecer el ID antes de actualizar
            contrato.id = id;
            
            Contrato contratoActualizado = contratoService.modificar(contrato);
            return Response.ok(contratoActualizado).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error actualizando contrato ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Eliminar un contrato (solo si pertenece al usuario autenticado)
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
            
            LOG.infof("Eliminando contrato ID: %d para usuario: %s", id, currentUser.email);
            
            // Verificar que el contrato existe y pertenece al usuario
            Optional<Contrato> contratoOpt = contratoService.listarPorId(id);
            if (contratoOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Contrato no encontrado\"}")
                        .build();
            }
            
            Contrato contrato = contratoOpt.get();
            boolean perteneceAlUsuario = contrato.unidad != null && 
                                       contrato.unidad.casa != null &&
                                       contrato.unidad.casa.usuarios.stream()
                                               .anyMatch(u -> u.id.equals(currentUser.id));
            
            if (!perteneceAlUsuario) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No tienes permisos para eliminar este contrato\"}")
                        .build();
            }
            
            contratoService.eliminar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.errorf(e, "Error eliminando contrato ID: %d", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint público para obtener información general sobre contratos
     */
    @GET
    @Path("/info")
    public Response info() {
        return Response.ok("{\"message\": \"Información pública de contratos disponible\"}")
                .build();
    }
}
