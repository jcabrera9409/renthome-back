package com.surrender.service.impl;

import java.time.YearMonth;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surrender.model.Contrato;
import com.surrender.model.DetalleRecibo;
import com.surrender.model.Recibo;
import com.surrender.repo.ContratoRepo;
import com.surrender.repo.ReciboRepo;
import com.surrender.service.IReciboService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReciboServiceImpl extends CRUDImpl<Recibo, Integer> implements IReciboService {

    private static final Logger logger = LoggerFactory.getLogger(ReciboServiceImpl.class);
    
    // Constante para validación
    private static final int MIN_SEARCH_LENGTH = 3;

    @Autowired
    private ReciboRepo reciboRepo;

    @Autowired
    private ContratoRepo contratoRepo;

    @Override
    protected ReciboRepo getRepo() {
        return reciboRepo;
    }

    @Override
    public List<Recibo> generarRecibosPorPeriodo(Integer casaId, YearMonth periodo) {
        logger.info("Iniciando generación de recibos para casa ID: {} y período: {}", casaId, periodo);
        
        List<Contrato> contratosActivos = contratoRepo.findByUnidadCasaIdAndActivoTrueOrderByFechaInicioDesc(casaId);
        logger.info("Se encontraron {} contratos activos para casa ID: {}", contratosActivos.size(), casaId);

        List<Contrato> contratosSinRecibo = contratosActivos.stream()
                .filter(contrato -> !reciboRepo.existsByPeriodoAndContrato(periodo, contrato))
                .toList();
        logger.info("De los {} contratos activos, {} no tienen recibo para el período {}", 
            contratosActivos.size(), contratosSinRecibo.size(), periodo);

        if (contratosSinRecibo.isEmpty()) {
            logger.info("No hay contratos sin recibo para generar en casa {} período {}", casaId, periodo);
            return List.of();
        }

        List<Recibo> recibosGenerados = contratosSinRecibo.stream()
                .map(contrato -> crearRecibo(contrato, periodo))
                .toList(); 

        List<Recibo> recibosPersistidos = reciboRepo.saveAll(recibosGenerados);
        logger.info("Se generaron y guardaron {} recibos exitosamente para casa {} período {}", 
            recibosPersistidos.size(), casaId, periodo);
        
        return recibosPersistidos;
    }

    @Override
    public Page<Recibo> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable) {
        logger.info("Filtrando recibos para casa ID: {} con filtro: '{}' y paginación", casaId, filtro);
        Page<Recibo> page;
        
        // Validar si el filtro es válido para búsqueda
        if (filtro == null || filtro.trim().isEmpty() || isInvalidSearchTerm(filtro.trim())) {
            // Sin filtro válido, devolver todos los recibos de la casa usando método JPA automático
            logger.info("Filtro no válido o vacío, devolviendo todos los recibos de la casa {}", casaId);
            page = reciboRepo.findByContratoUnidadCasaIdOrderByPeriodoDescContratoUnidadNombreAsc(casaId, pageable);
        } else {
            // Con filtro válido, usar búsqueda compleja con OR entre múltiples campos
            String filtroLimpio = filtro.trim();
            page = reciboRepo.findByCasaIdWithMultiFieldFilter(casaId, filtroLimpio, pageable);
        }
        
        logger.info("Se obtuvo página con {} elementos de {} total para casa {} con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), casaId, filtro);
        return page;
    }
    
    /**
     * Valida si un término de búsqueda es inválido o contiene solo caracteres especiales
     * @param searchTerm Término a validar
     * @return true si es inválido, false si es válido
     */
    private boolean isInvalidSearchTerm(String searchTerm) {
        if (searchTerm.length() < 1) {
            return true;
        }
        
        // Considerar inválidos los términos que solo contengan caracteres especiales comunes
        String specialCharsOnly = searchTerm.replaceAll("[a-zA-Z0-9\\s]", "");
        
        // Si después de quitar letras, números y espacios solo quedan caracteres especiales
        // y el término original era muy corto, considerarlo inválido
        return searchTerm.length() <= MIN_SEARCH_LENGTH && specialCharsOnly.length() == searchTerm.length();
    }

    private Recibo crearRecibo(Contrato contrato, YearMonth periodo) {
        logger.debug("Creando recibo para contrato ID: {} (unidad: {}, inquilino: {}) período: {}", 
            contrato.getId(), contrato.getUnidad().getNombre(), 
            contrato.getInquilino().getNombreCompleto(), periodo);
        
        Recibo recibo = new Recibo();
        recibo.setContrato(contrato);
        recibo.setPeriodo(periodo);
        recibo.setMontoTotal(contrato.getMontoRentaMensual());
        recibo.setPagado(false);
        
        List<DetalleRecibo> detalles = new java.util.ArrayList<>();
        
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setDescripcion("Alquiler");
        detalle.setMonto(contrato.getMontoRentaMensual());
        detalle.setRecibo(recibo); 
        
        detalles.add(detalle);
        
        recibo.setDetalle(detalles);
        
        logger.debug("Recibo creado para contrato ID: {} con monto total: {}", 
            contrato.getId(), contrato.getMontoRentaMensual());
        
        return recibo;
    }
}