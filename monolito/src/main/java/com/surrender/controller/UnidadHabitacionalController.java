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
import com.surrender.model.UnidadHabitacional;
import com.surrender.service.IUnidadHabitacionalService;

@RestController
@RequestMapping("/v1/unidades")
public class UnidadHabitacionalController {

    private static final Logger logger = LoggerFactory.getLogger(UnidadHabitacionalController.class);

    @Autowired
    private IUnidadHabitacionalService unidadHabitacionalService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<UnidadHabitacional>> registrar(@RequestBody UnidadHabitacional unidadHabitacional) {
        logger.info("Registrando nueva unidad habitacional: {}", unidadHabitacional.getNombre());
        UnidadHabitacional nueva = unidadHabitacionalService.registrar(unidadHabitacional);
        APIResponseDTO<UnidadHabitacional> response = (nueva != null)
            ? APIResponseDTO.success("Unidad habitacional registrada", nueva, 201)
            : APIResponseDTO.error("No se pudo registrar la unidad habitacional", 400);
        if (nueva != null) {
            logger.info("Unidad habitacional registrada exitosamente con id: {}", nueva.getId());
        } else {
            logger.warn("Fallo al registrar unidad habitacional: {}", unidadHabitacional.getNombre());
        }
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<UnidadHabitacional>> modificar(@RequestBody UnidadHabitacional unidadHabitacional) {
        logger.info("Modificando unidad habitacional con id: {}", unidadHabitacional.getId());
        UnidadHabitacional actualizada = unidadHabitacionalService.modificar(unidadHabitacional);
        APIResponseDTO<UnidadHabitacional> response = (actualizada != null)
            ? APIResponseDTO.success("Unidad habitacional modificada", actualizada, 200)
            : APIResponseDTO.error("No se pudo modificar la unidad habitacional", 400);
        if (actualizada != null) {
            logger.info("Unidad habitacional modificada exitosamente: {}", actualizada.getId());
        } else {
            logger.warn("Fallo al modificar unidad habitacional con id: {}", unidadHabitacional.getId());
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<UnidadHabitacional>> listarPorId(@PathVariable Integer id) {
        logger.info("Buscando unidad habitacional por id: {}", id);
        Optional<UnidadHabitacional> unidadHabitacional = unidadHabitacionalService.listarPorId(id);
        APIResponseDTO<UnidadHabitacional> response = unidadHabitacional
            .map(u -> APIResponseDTO.success("Unidad habitacional encontrada", u, 200))
            .orElseGet(() -> APIResponseDTO.error("Unidad habitacional no encontrada", 404));
        if (unidadHabitacional.isPresent()) {
            logger.info("Unidad habitacional encontrada: {}", unidadHabitacional.get().getId());
        } else {
            logger.warn("Unidad habitacional no encontrada con id: {}", id);
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping("/casa/{casaId}")
    public ResponseEntity<APIResponseDTO<List<UnidadHabitacional>>> listarTodos(@PathVariable Integer casaId) {
        logger.info("Listando todas las unidades habitacionales para casa ID: {}", casaId);
        List<UnidadHabitacional> lista = unidadHabitacionalService.listarPorCasa(casaId);
        APIResponseDTO<List<UnidadHabitacional>> response = APIResponseDTO.success("Lista de unidades habitacionales para casa " + casaId, lista, 200);
        logger.info("Se encontraron {} unidades para la casa {}", lista.size(), casaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/casa/{casaId}/disponibles")
    public ResponseEntity<APIResponseDTO<List<UnidadHabitacional>>> listarDisponibles(@PathVariable Integer casaId) {
        logger.info("Listando unidades habitacionales disponibles para casa ID: {}", casaId);
        List<UnidadHabitacional> disponibles = unidadHabitacionalService.listarDisponiblesPorCasa(casaId);
        APIResponseDTO<List<UnidadHabitacional>> response = APIResponseDTO.success("Lista de unidades habitacionales disponibles para casa " + casaId, disponibles, 200);
        logger.info("Se encontraron {} unidades disponibles para la casa {}", disponibles.size(), casaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/casa/{casaId}/filtrar")
    public ResponseEntity<APIResponseDTO<Page<UnidadHabitacional>>> filtrar(
            @PathVariable Integer casaId, 
            @RequestParam(required = false, defaultValue = "") String filtro,
            Pageable pageable) {
        logger.info("Filtrando unidades habitacionales para casa ID: {} con filtro: '{}' y paginación: página {}, tamaño {}", 
            casaId, filtro, pageable.getPageNumber(), pageable.getPageSize());
        Page<UnidadHabitacional> page = unidadHabitacionalService.filtrarPorCasa(casaId, filtro, pageable);
        APIResponseDTO<Page<UnidadHabitacional>> response = APIResponseDTO.success("Página de unidades habitacionales para casa " + casaId, page, 200);
        logger.info("Página obtenida: {} elementos de {} total para casa {} con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), casaId, filtro);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-estado")
    public ResponseEntity<APIResponseDTO<Void>> cambiarEstado(@RequestBody ChangeStatusRequestDTO request) {
        logger.info("Cambiando estado de unidad habitacional con id: {} a {}", request.getId(), request.getEstadoString());
        
        try {
            boolean cambiado = unidadHabitacionalService.cambiarEstado(request.getId(), request.getEstadoString());
            APIResponseDTO<Void> response = cambiado
                ? APIResponseDTO.success("Estado de unidad habitacional actualizado", null, 200)
                : APIResponseDTO.error("No se pudo cambiar el estado de la unidad habitacional", 400);
            
            if (cambiado) {
                logger.info("Estado de unidad habitacional cambiado exitosamente para id: {}", request.getId());
            } else {
                logger.warn("Fallo al cambiar estado de unidad habitacional con id: {}", request.getId());
            }
            return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al cambiar estado de unidad habitacional con id: {}: {}", request.getId(), e.getMessage());
            APIResponseDTO<Void> response = APIResponseDTO.error(e.getMessage(), 400);
            return ResponseEntity.status(400).body(response);
        } catch (RuntimeException e) {
            logger.warn("Error al cambiar estado de unidad habitacional con id: {}: {}", request.getId(), e.getMessage());
            APIResponseDTO<Void> response = APIResponseDTO.error(e.getMessage(), 404);
            return ResponseEntity.status(404).body(response);
        }
    }
}
