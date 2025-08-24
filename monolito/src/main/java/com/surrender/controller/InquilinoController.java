package com.surrender.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surrender.dto.APIResponseDTO;
import com.surrender.dto.ChangeStatusRequestDTO;
import com.surrender.model.Inquilino;
import com.surrender.service.IInquilinoService;

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
    public ResponseEntity<APIResponseDTO<List<Inquilino>>> listarTodos() {
        logger.info("Listando todos los inquilinos");
        List<Inquilino> lista = inquilinoService.listarTodos();
        APIResponseDTO<List<Inquilino>> response = APIResponseDTO.success("Lista de inquilinos", lista, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Inquilino>>> filtrar(
            @RequestParam(required = false, defaultValue = "") String filtro,
            Pageable pageable) {
        logger.info("Filtrando inquilinos con filtro: '{}' y paginación: página {}, tamaño {}", 
            filtro, pageable.getPageNumber(), pageable.getPageSize());
        Page<Inquilino> page = inquilinoService.filtrar(filtro, pageable);
        APIResponseDTO<Page<Inquilino>> response = APIResponseDTO.success("Página de inquilinos filtrados", page, 200);
        logger.info("Página obtenida: {} elementos de {} total con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), filtro);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<APIResponseDTO<List<Inquilino>>> listarDisponibles() {
        logger.info("Listando TODOS los inquilinos disponibles (activos)");
        List<Inquilino> inquilinos = inquilinoService.listarDisponibles();
        APIResponseDTO<List<Inquilino>> response = APIResponseDTO.success("Lista completa de inquilinos disponibles", inquilinos, 200);
        logger.info("Se obtuvieron {} inquilinos disponibles", inquilinos.size());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-estado")
    public ResponseEntity<APIResponseDTO<Void>> cambiarEstado(@RequestBody ChangeStatusRequestDTO request) {
        logger.info("Cambiando estado de inquilino con id: {} a {}", request.getId(), request.isEstado() ? "activo" : "inactivo");
        
        try {
            boolean cambiado = inquilinoService.cambiarEstado(request.getId(), request.isEstado());
            APIResponseDTO<Void> response = cambiado
                ? APIResponseDTO.success("Estado del inquilino actualizado", null, 200)
                : APIResponseDTO.error("No se pudo cambiar el estado del inquilino", 400);
            
            if (cambiado) {
                logger.info("Estado de inquilino cambiado exitosamente para id: {}", request.getId());
            } else {
                logger.warn("Fallo al cambiar estado de inquilino con id: {}", request.getId());
            }
            return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
            
        } catch (RuntimeException e) {
            logger.warn("Error de validación al cambiar estado de inquilino con id: {}: {}", request.getId(), e.getMessage());
            APIResponseDTO<Void> response = APIResponseDTO.error(e.getMessage(), 400);
            return ResponseEntity.status(400).body(response);
        }
    }
}
