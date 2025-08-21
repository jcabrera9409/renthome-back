package com.surrender.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surrender.model.UnidadHabitacional;
import com.surrender.repo.UnidadHabitacionalRepo;
import com.surrender.service.IUnidadHabitacionalService;

@Service
public class UnidadHabitacionalServiceImpl extends CRUDImpl<UnidadHabitacional, Integer> implements IUnidadHabitacionalService {

    private static final Logger logger = LoggerFactory.getLogger(UnidadHabitacionalServiceImpl.class);

    @Autowired
    private UnidadHabitacionalRepo unidadHabitacionalRepo;

    @Override
    protected UnidadHabitacionalRepo getRepo() {
        return unidadHabitacionalRepo;
    }

    @Override
    public List<UnidadHabitacional> listarPorCasa(Integer casaId) {
        logger.info("Listando unidades habitacionales para casa ID: {}", casaId);
        List<UnidadHabitacional> unidades = unidadHabitacionalRepo.findByCasaId(casaId);
        logger.info("Se encontraron {} unidades para la casa {}", unidades.size(), casaId);
        return unidades;
    }

    @Override
    public List<UnidadHabitacional> listarDisponiblesPorCasa(Integer casaId) {
        logger.info("Listando unidades habitacionales disponibles para casa ID: {}", casaId);
        List<UnidadHabitacional> unidadesDisponibles = unidadHabitacionalRepo.findByCasaIdAndEstado(casaId, "Disponible");
        logger.info("Se encontraron {} unidades disponibles para la casa {}", unidadesDisponibles.size(), casaId);
        return unidadesDisponibles;
    }

    @Override
    public Page<UnidadHabitacional> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable) {
        logger.info("Filtrando unidades habitacionales para casa ID: {} con filtro: '{}' y paginación", casaId, filtro);
        Page<UnidadHabitacional> page;
        
        // Validar si el filtro es válido para búsqueda
        if (filtro == null || filtro.trim().isEmpty() || isInvalidSearchTerm(filtro.trim())) {
            // Sin filtro válido, devolver todas las unidades de la casa
            logger.info("Filtro no válido o vacío, devolviendo todas las unidades de la casa {}", casaId);
            page = unidadHabitacionalRepo.findByCasaId(casaId, pageable);
        } else {
            // Con filtro válido, buscar por nombre, tipo de unidad o estado que contengan el texto
            String filtroLimpio = filtro.trim();
            page = unidadHabitacionalRepo.findByCasaIdAndNombreContainingIgnoreCaseOrTipoUnidadContainingIgnoreCaseOrEstadoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                casaId, filtroLimpio, filtroLimpio, filtroLimpio, filtroLimpio, pageable);
        }
        
        logger.info("Se obtuvo página con {} elementos de {} total para casa {} con filtro '{}'", 
            page.getNumberOfElements(), page.getTotalElements(), casaId, filtro);
        return page;
    }

    @Override
    public boolean cambiarEstado(Integer id, String estado) {
        logger.info("Cambiando estado de unidad con id: {} a {}", id, estado);
        
        Optional<UnidadHabitacional> unidadOpt = unidadHabitacionalRepo.findById(id);
        if (unidadOpt.isEmpty()) {
            logger.warn("No se encontró unidad con id: {}", id);
            throw new RuntimeException("Unidad habitacional no encontrada");
        }
        
        UnidadHabitacional unidad = unidadOpt.get();
        String estadoActual = unidad.getEstado();
        
        // Validación de regla de negocio: Solo se puede cambiar a Mantenimiento desde Disponible
        if (("Mantenimiento".equals(estado) && !"Disponible".equals(estadoActual)) || estado.equals("")) {
            logger.warn("No se puede cambiar a Mantenimiento la unidad con id: {} porque su estado actual es: {}", 
                id, estadoActual);
            throw new IllegalArgumentException(
                "Solo se puede cambiar a Mantenimiento unidades que estén Disponibles. Estado actual: " + estadoActual);
        }
        
        try {
            unidad.setEstado(estado);
            unidadHabitacionalRepo.save(unidad);
            logger.info("Estado de unidad con id: {} cambiado exitosamente de {} a {}", id, estadoActual, estado);
            return true;
        } catch (Exception e) {
            logger.error("Error al cambiar estado de unidad con id: {}: {}", id, e.getMessage());
            return false;
        }
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
}
