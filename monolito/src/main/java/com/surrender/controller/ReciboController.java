package com.surrender.controller;

import java.time.YearMonth;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surrender.dto.APIResponseDTO;
import com.surrender.model.Recibo;
import com.surrender.service.IReciboService;

@RestController
@RequestMapping("/v1/recibos")
public class ReciboController {
    private static final Logger logger = LoggerFactory.getLogger(ReciboController.class);

    @Autowired
    private IReciboService reciboService;

    @GetMapping("/casa/{casaId}/generar/{year}/{month}")
    public ResponseEntity<APIResponseDTO<List<Recibo>>> generarRecibos(@PathVariable Integer casaId, @PathVariable int year, @PathVariable int month) {
        logger.info("Generando recibos para el año {} y mes {} de la casa {}", year, month, casaId);
        
        try {
            YearMonth periodo = YearMonth.of(year, month);
            List<Recibo> recibos = reciboService.generarRecibosPorPeriodo(casaId, periodo);
            
            if(recibos.isEmpty()) {
                logger.info("No se generaron recibos nuevos para la casa {} en el periodo {}-{}", casaId, year, month);
                return ResponseEntity.ok(
                    APIResponseDTO.success("No se generaron recibos nuevos", recibos, HttpStatus.OK.value())
                );
            }

            logger.info("Se generaron {} recibos exitosamente para la casa {} en el periodo {}-{}", 
                recibos.size(), casaId, year, month);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponseDTO.success("Recibos generados exitosamente", recibos, HttpStatus.CREATED.value())
            );
        } catch (Exception e) {
            logger.error("Error al generar recibos para casa {} en periodo {}-{}: {}", casaId, year, month, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                APIResponseDTO.error("Error interno al generar recibos", HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/casa/{casaId}/filtrar/{year}/{month}")
    public ResponseEntity<APIResponseDTO<Page<Recibo>>> filtrar(
            @PathVariable Integer casaId,
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false, defaultValue = "") String filtro,
            Pageable pageable) {
        logger.info("Filtrando recibos para casa ID: {} con filtro: '{}', periodo: {}-{} y paginación: página {}, tamaño {}", 
            casaId, filtro, year, month, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            YearMonth periodo = YearMonth.of(year, month);
            Page<Recibo> page = reciboService.filtrarPorCasaYPeriodo(casaId, filtro, periodo, pageable);
            APIResponseDTO<Page<Recibo>> response = APIResponseDTO.success("Página de recibos para casa " + casaId, page, 200);
            logger.info("Página obtenida: {} elementos de {} total para casa {} con filtro '{}' y periodo {}-{}", 
                page.getNumberOfElements(), page.getTotalElements(), casaId, filtro, year, month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al filtrar recibos para casa {} con filtro '{}' y periodo {}-{}: {}", casaId, filtro, year, month, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                APIResponseDTO.error("Error interno al filtrar recibos", HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/casa/{casaId}/periodos")
    public ResponseEntity<APIResponseDTO<List<YearMonth>>> obtenerPeriodos(@PathVariable Integer casaId) {
        logger.info("Obteniendo periodos para casa ID: {}", casaId);
        
        try {
            List<YearMonth> periodos = reciboService.obtenerPeriodosPorCasa(casaId);
            APIResponseDTO<List<YearMonth>> response = APIResponseDTO.success("Periodos obtenidos exitosamente", periodos, HttpStatus.OK.value());
            logger.info("Se obtuvieron {} periodos para casa ID: {}", periodos.size(), casaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener periodos para casa {}: {}", casaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                APIResponseDTO.error("Error interno al obtener periodos", HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}