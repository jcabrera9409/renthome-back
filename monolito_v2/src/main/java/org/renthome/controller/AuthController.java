package org.renthome.controller;

import jakarta.annotation.security.PermitAll;
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
import org.renthome.dto.AuthResponse;
import org.renthome.dto.LoginRequest;
import org.renthome.dto.RegisterRequest;
import org.renthome.service.AuthService;

/**
 * Controlador REST para autenticación JWT
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    /**
     * Endpoint de registro de usuario
     */
    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        LOG.infof("Solicitud de registro para: %s", request.getEmail());

        try {
            AuthResponse response = authService.register(request);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (RuntimeException e) {
            LOG.errorf(e, "Error en registro para: %s", request.getEmail());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error interno en registro para: %s", request.getEmail());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint de login
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        LOG.infof("Solicitud de login para: %s", request.getEmail());

        try {
            AuthResponse response = authService.login(request);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            LOG.errorf(e, "Error en login para: %s", request.getEmail());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error interno en login para: %s", request.getEmail());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint protegido de prueba
     */
    @GET
    @Path("/profile")
    @RolesAllowed("user")
    public Response getProfile(@Context SecurityContext securityContext) {
        try {
            String userEmail = securityContext.getUserPrincipal().getName();
            String userName = jwt.getClaim("name");
            // Convertir el userId de manera segura
            Object userIdObj = jwt.getClaim("userId");
            Integer userId = userIdObj instanceof Number ? ((Number) userIdObj).intValue() : null;

            LOG.infof("Acceso al perfil para usuario: %s", userEmail);

            String response = String.format("{" +
                    "\"message\": \"Acceso autorizado\"," +
                    "\"user\": {" +
                    "\"id\": %d," +
                    "\"email\": \"%s\"," +
                    "\"name\": \"%s\"" +
                    "}," +
                    "\"timestamp\": \"%s\"" +
                    "}", userId, userEmail, userName, java.time.Instant.now());

            return Response.ok(response).build();

        } catch (Exception e) {
            LOG.errorf(e, "Error accediendo al perfil");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Endpoint público de prueba
     */
    @GET
    @Path("/public")
    @PermitAll
    public Response publicEndpoint() {
        String response = String.format("{" +
                "\"message\": \"Endpoint público funcionando\"," +
                "\"timestamp\": \"%s\"" +
                "}", java.time.Instant.now());

        return Response.ok(response).build();
    }
}
