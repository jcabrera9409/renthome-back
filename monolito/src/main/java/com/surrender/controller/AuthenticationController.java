package com.surrender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surrender.dto.AuthenticationResponse;
import com.surrender.model.Usuario;
import com.surrender.service.impl.AuthenticationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  private AuthenticationService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody Usuario request) {
      logger.info("Intentando autenticar usuario con email: {}", request.getEmail());
      AuthenticationResponse response = authService.authenticate(request);
      HttpStatus status = (response.getAccessToken() != null) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
      if (status == HttpStatus.OK) {
          logger.info("Autenticación exitosa para usuario: {}", request.getEmail());
      } else {
          logger.warn("Autenticación fallida para usuario: {}", request.getEmail());
      }
      return ResponseEntity.status(status).body(response);
  }
}
