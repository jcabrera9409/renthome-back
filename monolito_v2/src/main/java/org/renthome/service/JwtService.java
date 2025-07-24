package org.renthome.service;

import org.renthome.model.Usuario;

public interface JwtService {

    /**
     * Generar token JWT para un usuario
     */
    String generateToken(Usuario usuario);

    /**
     * Validar y extraer información de un token JWT
     */
    String extractUsername(String token);

    /**
     * Verificar si un token es válido
     */
    boolean isTokenValid(String token, String username);

    /**
     * Verificar si un token ha expirado
     */
    boolean isTokenExpired(String token);
}
