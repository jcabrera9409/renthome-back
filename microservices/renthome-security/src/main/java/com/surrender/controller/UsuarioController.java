package com.surrender.controller;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Usuario;
import com.surrender.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/auth/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @PostMapping
    public ResponseEntity<APIResponseDTO<Usuario>> registrar(@RequestBody Usuario usuario) {
        logger.info("Intentando registrar usuario con email: {}", usuario.getEmail());
        Usuario nuevo = usuarioService.registrar(usuario);
        APIResponseDTO<Usuario> response = (nuevo != null)
            ? APIResponseDTO.success("Usuario registrado", nuevo, 201)
            : APIResponseDTO.error("No se pudo registrar el usuario", 400);
        logger.info("Resultado registro usuario: {}", response.isSuccess() ? "Éxito" : "Fallo");
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Usuario>> modificar(@RequestBody Usuario usuario) {
        logger.info("Modificando usuario con id: {}", usuario.getId());
        Usuario actualizado = usuarioService.modificar(usuario);
        APIResponseDTO<Usuario> response = (actualizado != null)
            ? APIResponseDTO.success("Usuario modificado", actualizado, 200)
            : APIResponseDTO.error("No se pudo modificar el usuario", 400);
        logger.info("Resultado modificación usuario: {}", response.isSuccess() ? "Éxito" : "Fallo");
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        logger.info("Eliminando usuario con id: {}", id);
        try {
            usuarioService.eliminar(id);
            APIResponseDTO<Void> response = APIResponseDTO.success("Usuario eliminado", null, 200);
            logger.info("Usuario eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario con id {}: {}", id, e.getMessage());
            APIResponseDTO<Void> response = APIResponseDTO.error("No se pudo eliminar el usuario", 400);
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Usuario>> listarPorId(@PathVariable Integer id) {
        logger.info("Buscando usuario por id: {}", id);
        Optional<Usuario> usuario = usuarioService.listarPorId(id);
        APIResponseDTO<Usuario> response = usuario.map(u -> APIResponseDTO.success("Usuario encontrado", u, 200))
            .orElseGet(() -> APIResponseDTO.error("Usuario no encontrado", 404));
        logger.info("Resultado búsqueda usuario: {}", response.isSuccess() ? "Encontrado" : "No encontrado");
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Usuario>>> listarTodos() {
        logger.info("Listando todos los usuarios");
        Iterable<Usuario> lista = usuarioService.listarTodos();
        APIResponseDTO<Iterable<Usuario>> response = APIResponseDTO.success("Lista de usuarios", lista, 200);
        logger.info("Usuarios listados: {}", ((Collection<?>)lista).size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Usuario>>> filtrar(Pageable pageable) {
        logger.info("Filtrando usuarios con paginación: {}", pageable);
        Page<Usuario> page = usuarioService.filtrar(pageable);
        APIResponseDTO<Page<Usuario>> response = APIResponseDTO.success("Página de usuarios", page, 200);
        logger.info("Usuarios encontrados en página: {}", page.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
