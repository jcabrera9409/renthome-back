package com.surrender.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.surrender.model.Token;
import com.surrender.repo.TokenRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler de logout seguro: marca el token como cerrado en la base de datos.
 */
@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutHandler.class);

    private final TokenRepo tokenRepository;

    public CustomLogoutHandler(TokenRepo tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        logger.info("Procesando logout para token: {}", authHeader);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Logout fallido: header Authorization inválido o ausente");
            return;
        }
        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);
        if(storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
            logger.info("Token marcado como cerrado exitosamente");
        } else {
            logger.warn("No se encontró token en base de datos para logout");
        }
    }
}
