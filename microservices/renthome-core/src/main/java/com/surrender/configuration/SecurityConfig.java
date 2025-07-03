package com.surrender.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Configuración de la cadena de filtros de seguridad para OAuth2 Resource Server.
     * Configura el servicio como un Resource Server que valida tokens JWT.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando SecurityFilterChain para renthome-core como OAuth2 Resource Server");
        
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                )
            )
            .build();
    }

    /**
     * Decodificador JWT que utiliza la misma clave secreta que el servicio de autenticación.
     * Usa BASE64URL decoding igual que JwtService en renthome-security.
     * Detecta automáticamente el algoritmo HMAC basado en la longitud de la clave.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        logger.info("Configurando JwtDecoder con clave secreta");
        try {
            // Usar el mismo método que JwtService.getSigninKey()
            byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
            SecretKey secretKeySpec = Keys.hmacShaKeyFor(keyBytes);
            
            // Determinar el algoritmo basado en la longitud de la clave
            // Esto replica la lógica de Keys.hmacShaKeyFor()
            MacAlgorithm algorithm;
            int keyLength = keyBytes.length * 8; // bits
            
            if (keyLength >= 512) {
                algorithm = MacAlgorithm.HS512;
                logger.info("Usando algoritmo HS512 (clave de {} bits)", keyLength);
            } else if (keyLength >= 384) {
                algorithm = MacAlgorithm.HS384;
                logger.info("Usando algoritmo HS384 (clave de {} bits)", keyLength);
            } else {
                algorithm = MacAlgorithm.HS256;
                logger.info("Usando algoritmo HS256 (clave de {} bits)", keyLength);
            }
            
            return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(algorithm)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error configurando JwtDecoder: {}", e.getMessage());
            throw new RuntimeException("Error configurando JWT decoder", e);
        }
    }
}
