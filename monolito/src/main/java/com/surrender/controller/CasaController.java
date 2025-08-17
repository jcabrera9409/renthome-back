package com.surrender.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Casa;
import com.surrender.service.ICasaService;
import com.surrender.service.impl.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/casas")
public class CasaController {

    private static final Logger logger = LoggerFactory.getLogger(CasaController.class);

    @Autowired
    private ICasaService casaService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/mis-casas")
    public ResponseEntity<APIResponseDTO<List<Casa>>> listarPorUsuario(HttpServletRequest request) {
        try {
            // Extraer token del header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Header Authorization inválido o ausente");
                APIResponseDTO<List<Casa>> response = APIResponseDTO.error("Token de autorización requerido", 401);
                return ResponseEntity.status(401).body(response);
            }

            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token); // El claim 'sub' contiene el email/username

            logger.info("Listando casas para usuario con email extraído del token: {}", email);
            
            List<Casa> casas = casaService.listarPorUsuario(email);
            APIResponseDTO<List<Casa>> response = APIResponseDTO.success("Lista de casas del usuario", casas, 200);
            
            logger.info("Se retornaron {} casas para el usuario: {}", casas.size(), email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al listar casas por usuario: {}", e.getMessage());
            APIResponseDTO<List<Casa>> response = APIResponseDTO.error("Error interno del servidor", 500);
            return ResponseEntity.status(500).body(response);
        }
    }
}
