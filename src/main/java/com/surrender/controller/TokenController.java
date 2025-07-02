package com.surrender.controller;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Token;
import com.surrender.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/v1/tokens")
public class TokenController {

    @Autowired
    private ITokenService tokenService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<Token>> registrar(@RequestBody Token token) {
        Token nuevo = tokenService.registrar(token);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Token registrado", nuevo, 200));
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Token>> modificar(@RequestBody Token token) {
        Token actualizado = tokenService.modificar(token);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Token modificado", actualizado, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        tokenService.eliminar(id);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Token eliminado", null, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Token>> listarPorId(@PathVariable Integer id) {
        Optional<Token> token = tokenService.listarPorId(id);
        if (token.isPresent()) {
            return ResponseEntity.ok(new APIResponseDTO<>(true, "Token encontrado", token.get(), 200));
        } else {
            return ResponseEntity.status(404).body(new APIResponseDTO<>(false, "Token no encontrado", null, 404));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Token>>> listarTodos() {
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Lista de tokens", tokenService.listarTodos(), 200));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Token>>> filtrar(Pageable pageable) {
        Page<Token> page = tokenService.filtrar(pageable);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Página de tokens", page, 200));
    }
}
