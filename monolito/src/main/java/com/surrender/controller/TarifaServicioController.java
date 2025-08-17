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
import org.springframework.web.bind.annotation.RestController;

import com.surrender.dto.APIResponseDTO;
import com.surrender.dto.ChangeStatusRequestDTO;
import com.surrender.model.TarifaServicio;
import com.surrender.service.ITarifaServicioService;

@RestController
@RequestMapping("/v1/tarifas")
public class TarifaServicioController {

    private static final Logger logger = LoggerFactory.getLogger(TarifaServicioController.class);

    @Autowired
    private ITarifaServicioService tarifaServicioService;

    @PostMapping
    public ResponseEntity<APIResponseDTO<TarifaServicio>> registrar(@RequestBody TarifaServicio tarifaServicio) {
        logger.info("Registrando nueva tarifa de servicio: {}", tarifaServicio.getTipoServicio());
        TarifaServicio nueva = tarifaServicioService.registrar(tarifaServicio);
        APIResponseDTO<TarifaServicio> response = (nueva != null)
            ? APIResponseDTO.success("Tarifa de servicio registrada", nueva, 201)
            : APIResponseDTO.error("No se pudo registrar la tarifa de servicio", 400);
        if (nueva != null) {
            logger.info("Tarifa de servicio registrada exitosamente con id: {}", nueva.getId());
        } else {
            logger.warn("Fallo al registrar tarifa de servicio: {}", tarifaServicio.getTipoServicio());
        }
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping
    public ResponseEntity<APIResponseDTO<TarifaServicio>> modificar(@RequestBody TarifaServicio tarifaServicio) {
        logger.info("Modificando tarifa de servicio con id: {}", tarifaServicio.getId());
        TarifaServicio actualizada = tarifaServicioService.modificar(tarifaServicio);
        APIResponseDTO<TarifaServicio> response = (actualizada != null)
            ? APIResponseDTO.success("Tarifa de servicio modificada", actualizada, 200)
            : APIResponseDTO.error("No se pudo modificar la tarifa de servicio", 400);
        if (actualizada != null) {
            logger.info("Tarifa de servicio modificada exitosamente: {}", actualizada.getId());
        } else {
            logger.warn("Fallo al modificar tarifa de servicio con id: {}", tarifaServicio.getId());
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponseDTO<TarifaServicio>> listarPorId(@PathVariable Integer id) {
        logger.info("Buscando tarifa de servicio por id: {}", id);
        Optional<TarifaServicio> tarifaServicio = tarifaServicioService.listarPorId(id);
        APIResponseDTO<TarifaServicio> response = tarifaServicio
            .map(t -> APIResponseDTO.success("Tarifa de servicio encontrada", t, 200))
            .orElseGet(() -> APIResponseDTO.error("Tarifa de servicio no encontrada", 404));
        if (tarifaServicio.isPresent()) {
            logger.info("Tarifa de servicio encontrada: {}", tarifaServicio.get().getId());
        } else {
            logger.warn("Tarifa de servicio no encontrada con id: {}", id);
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<List<TarifaServicio>>> listarTodos() {
        logger.info("Listando todas las tarifas de servicio");
        List<TarifaServicio> lista = tarifaServicioService.listarTodos();
        APIResponseDTO<List<TarifaServicio>> response = APIResponseDTO.success("Lista de tarifas de servicio", lista, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activos")
    public ResponseEntity<APIResponseDTO<List<TarifaServicio>>> listarActivos() {
        logger.info("Listando tarifas de servicio activas");
        List<TarifaServicio> activos = tarifaServicioService.listarActivos();
        APIResponseDTO<List<TarifaServicio>> response = APIResponseDTO.success("Lista de tarifas de servicio activas", activos, 200);
        logger.info("Se encontraron {} tarifas activas", activos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<APIResponseDTO<Page<TarifaServicio>>> filtrar(Pageable pageable) {
        logger.info("Filtrando tarifas de servicio con paginación: página {}, tamaño {}", 
            pageable.getPageNumber(), pageable.getPageSize());
        Page<TarifaServicio> page = tarifaServicioService.filtrar(pageable);
        APIResponseDTO<Page<TarifaServicio>> response = APIResponseDTO.success("Página de tarifas de servicio", page, 200);
        logger.info("Página obtenida: {} elementos de {} total", page.getNumberOfElements(), page.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-estado")
    public ResponseEntity<APIResponseDTO<Void>> cambiarEstado(@RequestBody ChangeStatusRequestDTO request) {
        logger.info("Cambiando estado de tarifa de servicio con id: {} a {}", request.getId(), request.isEstado() ? "activo" : "inactivo");
        boolean cambiado = tarifaServicioService.cambiarEstado(request.getId(), request.isEstado());
        APIResponseDTO<Void> response = cambiado
            ? APIResponseDTO.success("Estado de tarifa de servicio actualizado", null, 200)
            : APIResponseDTO.error("No se pudo cambiar el estado de la tarifa de servicio", 400);
        if (cambiado) {
            logger.info("Estado de tarifa de servicio cambiado exitosamente para id: {}", request.getId());
        } else {
            logger.warn("Fallo al cambiar estado de tarifa de servicio con id: {}", request.getId());
        }
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
}
