package org.renthome.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.IUsuarioService;

import java.util.Optional;

/**
 * Controlador REST para gestión de Usuarios con autenticación JWT
 */
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    private static final Logger LOG = Logger.getLogger(UsuarioController.class);

    @Inject
    IUsuarioService usuarioService;

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
     * Obtener el perfil del usuario autenticado
     */
    @GET
    @Path("/perfil")
    @RolesAllowed("user")
    public Response obtenerPerfil() {
        LOG.info("Obteniendo perfil del usuario autenticado");
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                LOG.error("getCurrentUser() retornó null");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Obteniendo perfil para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // No devolver la contraseña en la respuesta
            currentUser.password = null;
            
            return Response.ok(currentUser).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error obteniendo perfil del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Actualizar el perfil del usuario autenticado
     */
    @PUT
    @Path("/perfil")
    @RolesAllowed("user")
    public Response actualizarPerfil(@Valid Usuario usuarioActualizado) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Actualizando perfil para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // Solo permitir actualizar ciertos campos
            currentUser.nombre = usuarioActualizado.nombre;
            currentUser.email = usuarioActualizado.email;
            
            // No permitir cambio de contraseña a través de este endpoint por seguridad
            // Se requeriría un endpoint específico con validación adicional
            
            Usuario usuarioGuardado = usuarioService.modificar(currentUser);
            
            // No devolver la contraseña en la respuesta
            usuarioGuardado.password = null;
            
            return Response.ok(usuarioGuardado).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error actualizando perfil del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Cambiar contraseña del usuario autenticado
     */
    @PUT
    @Path("/cambiar-password")
    @RolesAllowed("user")
    public Response cambiarPassword(CambiarPasswordRequest request) {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Cambiando contraseña para usuario: %s", currentUser.email);
            
            // Validar que se proporcionen los datos necesarios
            if (request.passwordActual == null || request.passwordNuevo == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Debe proporcionar la contraseña actual y la nueva\"}")
                        .build();
            }
            
            // Aquí deberías implementar la lógica de cambio de contraseña
            // Verificar contraseña actual, encriptar nueva contraseña, etc.
            boolean passwordCambiado = usuarioService.cambiarPassword(
                currentUser.id, 
                request.passwordActual, 
                request.passwordNuevo
            );
            
            if (!passwordCambiado) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"La contraseña actual es incorrecta\"}")
                        .build();
            }
            
            return Response.ok("{\"message\": \"Contraseña actualizada correctamente\"}")
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error cambiando contraseña del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Eliminar la cuenta del usuario autenticado
     */
    @DELETE
    @Path("/perfil")
    @RolesAllowed("user")
    public Response eliminarCuenta() {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Usuario no encontrado\"}")
                        .build();
            }
            
            LOG.infof("Eliminando cuenta para usuario: %s (ID: %d)", currentUser.email, currentUser.id);
            
            // Marcar como inactivo en lugar de eliminar físicamente
            // Esto preserva la integridad referencial de contratos, casas, etc.
            currentUser.activo = false;
            usuarioService.modificar(currentUser);
            
            return Response.ok("{\"message\": \"Cuenta desactivada correctamente\"}")
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error eliminando cuenta del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint público para obtener información general sobre usuarios
     */
    @GET
    @Path("/info")
    public Response info() {
        return Response.ok("{\"message\": \"Información pública de usuarios disponible\"}")
                .build();
    }

    /**
     * Clase interna para el request de cambio de contraseña
     */
    public static class CambiarPasswordRequest {
        public String passwordActual;
        public String passwordNuevo;
    }
}
