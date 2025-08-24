package com.surrender.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surrender.model.Inquilino;
import com.surrender.repo.ContratoRepo;
import com.surrender.repo.InquilinoRepo;
import com.surrender.service.IInquilinoService;

@Service
public class InquilinoServiceImpl extends CRUDImpl<Inquilino, Integer> implements IInquilinoService {

    private static final Logger logger = LoggerFactory.getLogger(InquilinoServiceImpl.class);

    @Autowired
    private InquilinoRepo inquilinoRepo;

    @Autowired
    private ContratoRepo contratoRepo;

    @Override
    protected InquilinoRepo getRepo() {
        logger.debug("Obteniendo repositorio de inquilinos");
        return inquilinoRepo;
    }

    @Override
    public List<Inquilino> listarDisponibles() {
        logger.info("Listando todos los inquilinos disponibles (activos)");
        List<Inquilino> inquilinos = inquilinoRepo.findByActivoTrueOrderByNombreCompletoAsc();
        logger.info("Se encontraron {} inquilinos disponibles", inquilinos.size());
        return inquilinos;
    }

    @Override
    public Page<Inquilino> filtrar(String filtro, Pageable pageable) {
        logger.info("Filtrando inquilinos con filtro: '{}' y paginación", filtro);
        Page<Inquilino> page;
        
        // Validar si el filtro es válido para búsqueda
        if (filtro == null || filtro.trim().isEmpty() || isInvalidSearchTerm(filtro.trim())) {
            // Sin filtro válido, devolver todos los inquilinos usando método JPA automático del padre
            logger.info("Filtro no válido o vacío, devolviendo todos los inquilinos");
            page = super.filtrar(pageable);
        } else {
            // Con filtro válido, usar búsqueda JPA con OR entre múltiples campos
            String filtroLimpio = filtro.trim();
            // Usar el mismo filtro para todos los campos en la búsqueda OR
            page = inquilinoRepo.findByNombreCompletoContainingIgnoreCaseOrDocumentoIdentidadContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreCompletoAsc(
                filtroLimpio, filtroLimpio, filtroLimpio, filtroLimpio, pageable);
        }
        
        logger.info("Se obtuvo página con {} elementos de {} total con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), filtro);
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
        return searchTerm.length() <= 2 && specialCharsOnly.length() == searchTerm.length();
    }

    @Override
    public boolean cambiarEstado(Integer id, boolean estado) {
        logger.info("Cambiando estado de inquilino con id: {} a {}", id, estado ? "activo" : "inactivo");
        try {
            Optional<Inquilino> inquilinoOpt = inquilinoRepo.findById(id);
            if (inquilinoOpt.isPresent()) {
                Inquilino inquilino = inquilinoOpt.get();
                
                // Validación: No permitir inactivar inquilino con contratos activos
                if (!estado && tieneContratosActivos(id)) {
                    logger.warn("No se puede inactivar el inquilino con id: {} porque tiene contratos activos", id);
                    throw new RuntimeException("No se puede inactivar el inquilino porque tiene contratos activos");
                }
                
                inquilino.setActivo(estado);
                inquilinoRepo.save(inquilino);
                logger.info("Estado de inquilino con id: {} cambiado exitosamente a {}", id, estado ? "activo" : "inactivo");
                return true;
            } else {
                logger.warn("No se encontró inquilino con id: {}", id);
                return false;
            }
        } catch (RuntimeException e) {
            // Re-lanzar excepciones de validación para manejo en el controller
            logger.error("Error de validación al cambiar estado del inquilino con id: {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al cambiar estado del inquilino con id: {}", id, e);
            return false;
        }
    }
    
    /**
     * Verifica si un inquilino tiene contratos activos
     * @param inquilinoId ID del inquilino a verificar
     * @return true si tiene contratos activos, false en caso contrario
     */
    private boolean tieneContratosActivos(Integer inquilinoId) {
        boolean tieneContratos = contratoRepo.existsByInquilinoIdAndActivoTrue(inquilinoId);
        logger.debug("Inquilino {} {} contratos activos", inquilinoId, tieneContratos ? "tiene" : "no tiene");
        return tieneContratos;
    }
}
