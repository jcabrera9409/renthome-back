package org.renthome.service;

import org.renthome.dto.AuthResponse;
import org.renthome.dto.LoginRequest;
import org.renthome.dto.RegisterRequest;

public interface AuthService {

    /**
     * Registrar un nuevo usuario
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Autenticar usuario y generar token
     */
    AuthResponse login(LoginRequest request);

    /**
     * Verificar si un email ya está en uso
     */
    boolean existsByEmail(String email);
}
