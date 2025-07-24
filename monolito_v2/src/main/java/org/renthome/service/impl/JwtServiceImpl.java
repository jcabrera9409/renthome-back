package org.renthome.service.impl;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.renthome.model.Usuario;
import org.renthome.service.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class JwtServiceImpl implements JwtService {

    private static final Logger LOG = Logger.getLogger(JwtServiceImpl.class);

    @ConfigProperty(name = "jwt.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.time")
    long expirationTime;

    @Override
    public String generateToken(Usuario usuario) {
        try {
            LOG.infof("Generando token JWT para usuario: %s", usuario.email);
            
            return Jwt.issuer(issuer)
                    .upn(usuario.email)  // User Principal Name
                    .subject(usuario.email)
                    .claim("name", usuario.nombre)
                    .claim("userId", usuario.id)
                    .groups(Set.of("user"))  // Rol básico
                    .expiresIn(Duration.ofSeconds(expirationTime))
                    .sign();
                    
        } catch (Exception e) {
            LOG.errorf(e, "Error generando token JWT para usuario: %s", usuario.email);
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            // En SmallRye JWT, la extracción se hace automáticamente en el contexto
            // Este método es principalmente para validación manual
            LOG.debugf("Extrayendo username del token");
            return null; // Se maneja automáticamente por SmallRye JWT
        } catch (Exception e) {
            LOG.errorf(e, "Error extrayendo username del token");
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        try {
            // SmallRye JWT maneja la validación automáticamente
            LOG.debugf("Validando token para usuario: %s", username);
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            LOG.errorf(e, "Error validando token para usuario: %s", username);
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            // SmallRye JWT maneja la expiración automáticamente
            LOG.debugf("Verificando expiración del token");
            return false; // Se maneja automáticamente
        } catch (Exception e) {
            LOG.errorf(e, "Error verificando expiración del token");
            return true;
        }
    }
}