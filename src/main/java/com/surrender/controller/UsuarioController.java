package com.surrender.controller;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Usuario;
import com.surrender.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<Usuario>> registrar(@RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.registrar(usuario);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Usuario registrado", nuevo, 200));
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Usuario>> modificar(@RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.modificar(usuario);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Usuario modificado", actualizado, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Usuario eliminado", null, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Usuario>> listarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.listarPorId(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(new APIResponseDTO<>(true, "Usuario encontrado", usuario.get(), 200));
        } else {
            return ResponseEntity.status(404).body(new APIResponseDTO<>(false, "Usuario no encontrado", null, 404));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Usuario>>> listarTodos() {
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Lista de usuarios", usuarioService.listarTodos(), 200));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Usuario>>> filtrar(Pageable pageable) {
        Page<Usuario> page = usuarioService.filtrar(pageable);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Página de usuarios", page, 200));
    }
}
