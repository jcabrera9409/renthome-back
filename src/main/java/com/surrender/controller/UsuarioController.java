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
        APIResponseDTO<Usuario> response = (nuevo != null)
            ? APIResponseDTO.success("Usuario registrado", nuevo, 201)
            : APIResponseDTO.error("No se pudo registrar el usuario", 400);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Usuario>> modificar(@RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.modificar(usuario);
        APIResponseDTO<Usuario> response = (actualizado != null)
            ? APIResponseDTO.success("Usuario modificado", actualizado, 200)
            : APIResponseDTO.error("No se pudo modificar el usuario", 400);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.eliminar(id);
            APIResponseDTO<Void> response = APIResponseDTO.success("Usuario eliminado", null, 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            APIResponseDTO<Void> response = APIResponseDTO.error("No se pudo eliminar el usuario", 400);
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Usuario>> listarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.listarPorId(id);
        APIResponseDTO<Usuario> response = usuario.map(u -> APIResponseDTO.success("Usuario encontrado", u, 200))
            .orElseGet(() -> APIResponseDTO.error("Usuario no encontrado", 404));
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Usuario>>> listarTodos() {
        Iterable<Usuario> lista = usuarioService.listarTodos();
        APIResponseDTO<Iterable<Usuario>> response = APIResponseDTO.success("Lista de usuarios", lista, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Usuario>>> filtrar(Pageable pageable) {
        Page<Usuario> page = usuarioService.filtrar(pageable);
        APIResponseDTO<Page<Usuario>> response = APIResponseDTO.success("Página de usuarios", page, 200);
        return ResponseEntity.ok(response);
    }
}
