package com.surrender.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.surrender.service.impl.JwtService;
import com.surrender.service.impl.UserDetailsServiceImpl;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filtro JWT para validar y autenticar cada petición HTTP.
 * Si el token es válido, se establece el usuario autenticado en el contexto de seguridad.
 */
@Component
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
        logger.debug("Procesando filtro JWT para request: {} {}", request.getMethod(), request.getRequestURI());
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No se encontró header Authorization válido, continuando cadena de filtros");
            filterChain.doFilter(request,response);
            return;
        }
        String token = authHeader.substring(7);
        String correo = jwtService.extractUsername(token);
        logger.debug("Token extraído para usuario: {}", correo);
        if(correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
            if(jwtService.isValid(token, userDetails)) {
                logger.info("Token JWT válido para usuario: {}", correo);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Token JWT inválido para usuario: {}", correo);
            }
        }
        filterChain.doFilter(request, response);
    }
}
