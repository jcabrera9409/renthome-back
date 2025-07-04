package com.surrender.controller;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Inquilino;
import com.surrender.service.IInquilinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@RestController
@RequestMapping("/v1/inquilinos")
public class InquilinoController {

    private static final Logger logger = LoggerFactory.getLogger(InquilinoController.class);

    @Autowired
    private IInquilinoService inquilinoService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<Inquilino>> registrar(@RequestBody Inquilino inquilino) {
        logger.info("Registrando nuevo inquilino: {}", inquilino);
        Inquilino nuevo = inquilinoService.registrar(inquilino);
        APIResponseDTO<Inquilino> response = (nuevo != null)
            ? APIResponseDTO.success("Inquilino registrado", nuevo, 201)
            : APIResponseDTO.error("No se pudo registrar el inquilino", 400);
        if (nuevo != null) {
            logger.info("Inquilino registrado exitosamente: {}", nuevo);
        } else {
            logger.warn("Fallo al registrar inquilino: {}", inquilino);
        }
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Inquilino>> modificar(@RequestBody Inquilino inquilino) {
        logger.info("Modificando inquilino: {}", inquilino);
        Inquilino actualizado = inquilinoService.modificar(inquilino);
        APIResponseDTO<Inquilino> response = (actualizado != null)
            ? APIResponseDTO.success("Inquilino modificado", actualizado, 200)
            : APIResponseDTO.error("No se pudo modificar el inquilino", 400);
        if (actualizado != null) {
            logger.info("Inquilino modificado exitosamente: {}", actualizado);
        } else {
            logger.warn("Fallo al modificar inquilino: {}", inquilino);
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Void>> eliminar(@PathVariable Integer id) {
        logger.info("Eliminando inquilino con id: {}", id);
        try {
            inquilinoService.eliminar(id);
            APIResponseDTO<Void> response = APIResponseDTO.success("Inquilino eliminado", null, 200);
            logger.info("Inquilino eliminado con id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar inquilino con id: {}: {}", id, e.getMessage());
            APIResponseDTO<Void> response = APIResponseDTO.error("No se pudo eliminar el inquilino", 400);
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Inquilino>> listarPorId(@PathVariable Integer id) {
        logger.info("Buscando inquilino por id: {}", id);
        Optional<Inquilino> inquilino = inquilinoService.listarPorId(id);
        APIResponseDTO<Inquilino> response = inquilino.map(i -> APIResponseDTO.success("Inquilino encontrado", i, 200))
            .orElseGet(() -> APIResponseDTO.error("Inquilino no encontrado", 404));
        if (inquilino.isPresent()) {
            logger.info("Inquilino encontrado: {}", inquilino.get());
        } else {
            logger.warn("Inquilino no encontrado con id: {}", id);
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Iterable<Inquilino>>> listarTodos() {
        logger.info("Listando todos los inquilinos");
        Iterable<Inquilino> lista = inquilinoService.listarTodos();
        APIResponseDTO<Iterable<Inquilino>> response = APIResponseDTO.success("Lista de inquilinos", lista, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Inquilino>>> filtrar(Pageable pageable) {
        Page<Inquilino> page = inquilinoService.filtrar(pageable);
        APIResponseDTO<Page<Inquilino>> response = APIResponseDTO.success("Página de inquilinos", page, 200);
        return ResponseEntity.ok(response);
    }
}
