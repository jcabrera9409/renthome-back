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

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

  @Autowired
  private AuthenticationService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody Usuario request) {
      AuthenticationResponse response = authService.authenticate(request);
      HttpStatus status = (response.getAccessToken() != null) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
      return ResponseEntity.status(status).body(response);
  }
}
