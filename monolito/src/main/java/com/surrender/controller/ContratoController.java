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
import com.surrender.model.Contrato;
import com.surrender.service.IContratoService;

@RestController
@RequestMapping("/v1/contratos")
public class ContratoController {

    private static final Logger logger = LoggerFactory.getLogger(ContratoController.class);

    @Autowired
    private IContratoService contratoService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<Contrato>> registrar(@RequestBody Contrato contrato) {
        logger.info("Registrando nuevo contrato para inquilino: {} y unidad: {}", 
            contrato.getInquilino().getNombreCompleto(), contrato.getUnidad().getNombre());
        Contrato nuevo = contratoService.registrar(contrato);
        APIResponseDTO<Contrato> response = (nuevo != null)
            ? APIResponseDTO.success("Contrato registrado", nuevo, 201)
            : APIResponseDTO.error("No se pudo registrar el contrato", 400);
        if (nuevo != null) {
            logger.info("Contrato registrado exitosamente con id: {}", nuevo.getId());
        } else {
            logger.warn("Fallo al registrar contrato para inquilino: {}", contrato.getInquilino().getNombreCompleto());
        }
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<Contrato>> modificar(@RequestBody Contrato contrato) {
        logger.info("Modificando contrato con id: {}", contrato.getId());
        Contrato actualizado = contratoService.modificar(contrato);
        APIResponseDTO<Contrato> response = (actualizado != null)
            ? APIResponseDTO.success("Contrato modificado", actualizado, 200)
            : APIResponseDTO.error("No se pudo modificar el contrato", 400);
        if (actualizado != null) {
            logger.info("Contrato modificado exitosamente: {}", actualizado.getId());
        } else {
            logger.warn("Fallo al modificar contrato con id: {}", contrato.getId());
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<Contrato>> listarPorId(@PathVariable Integer id) {
        logger.info("Buscando contrato por id: {}", id);
        Optional<Contrato> contrato = contratoService.listarPorId(id);
        APIResponseDTO<Contrato> response = contrato
            .map(c -> APIResponseDTO.success("Contrato encontrado", c, 200))
            .orElseGet(() -> APIResponseDTO.error("Contrato no encontrado", 404));
        if (contrato.isPresent()) {
            logger.info("Contrato encontrado: {}", contrato.get().getId());
        } else {
            logger.warn("Contrato no encontrado con id: {}", id);
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping("/casa/{casaId}/filtrar")
    public ResponseEntity<APIResponseDTO<Page<Contrato>>> filtrar(
            @PathVariable Integer casaId,
            @RequestParam(required = false, defaultValue = "") String filtro,
            Pageable pageable) {
        logger.info("Filtrando contratos para casa ID: {} con filtro: '{}' y paginación: página {}, tamaño {}", 
            casaId, filtro, pageable.getPageNumber(), pageable.getPageSize());
        Page<Contrato> page = contratoService.filtrarPorCasa(casaId, filtro, pageable);
        APIResponseDTO<Page<Contrato>> response = APIResponseDTO.success("Página de contratos para casa " + casaId, page, 200);
        logger.info("Página obtenida: {} elementos de {} total para casa {} con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), casaId, filtro);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/casa/{casaId}/disponibles")
    public ResponseEntity<APIResponseDTO<List<Contrato>>> listarActivos(
            @PathVariable Integer casaId) {
        logger.info("Listando TODOS los contratos activos para casa ID: {}", casaId);
        List<Contrato> contratos = contratoService.listarActivosPorCasa(casaId);
        APIResponseDTO<List<Contrato>> response = APIResponseDTO.success("Lista completa de contratos activos para casa " + casaId, contratos, 200);
        logger.info("Se obtuvieron {} contratos activos para casa {}", contratos.size(), casaId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-estado")
    public ResponseEntity<APIResponseDTO<Void>> cambiarEstado(@RequestBody ChangeStatusRequestDTO request) {
        logger.info("Cambiando estado de contrato con id: {} a {}", request.getId(), request.isEstado() ? "activo" : "inactivo");
        boolean cambiado = contratoService.cambiarEstado(request.getId(), request.isEstado());
        APIResponseDTO<Void> response = cambiado
            ? APIResponseDTO.success("Estado del contrato actualizado", null, 200)
            : APIResponseDTO.error("No se pudo cambiar el estado del contrato", 400);
        if (cambiado) {
            logger.info("Estado de contrato cambiado exitosamente para id: {}", request.getId());
        } else {
            logger.warn("Fallo al cambiar estado de contrato con id: {}", request.getId());
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
}
