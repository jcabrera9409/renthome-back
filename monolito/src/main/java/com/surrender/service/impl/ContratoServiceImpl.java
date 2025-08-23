package com.surrender.service.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surrender.model.Contrato;
import com.surrender.model.UnidadHabitacional;
import com.surrender.repo.ContratoRepo;
import com.surrender.repo.UnidadHabitacionalRepo;
import com.surrender.service.IContratoService;

@Service
@Transactional
public class ContratoServiceImpl extends CRUDImpl<Contrato, Integer> implements IContratoService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoServiceImpl.class);
    
    // Constantes para estados de unidad
    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_OCUPADO = "Ocupado";
    
    // Constantes para validación
    private static final int MIN_SEARCH_LENGTH = 3;

    @Autowired
    private ContratoRepo contratoRepo;

    @Autowired
    private UnidadHabitacionalRepo unidadHabitacionalRepo;

    @Override
    protected ContratoRepo getRepo() {
        return contratoRepo;
    }

    @Override
    public Contrato registrar(Contrato contrato) {
        logger.info("Registrando nuevo contrato para inquilino: {} y unidad: {}", 
            contrato.getInquilino().getNombreCompleto(), contrato.getUnidad().getId());
        
        // Validar que la unidad esté disponible
        UnidadHabitacional unidad = obtenerUnidadPorId(contrato.getUnidad().getId());
        validarUnidadDisponible(unidad);
        
        // Marcar unidad como ocupada y guardar contrato
        actualizarEstadoUnidad(unidad, ESTADO_OCUPADO);
        Contrato contratoGuardado = contratoRepo.save(contrato);
        
        logger.info("Contrato registrado exitosamente con id: {}", contratoGuardado.getId());
        return contratoGuardado;
    }

    @Override
    public Contrato modificar(Contrato contrato) {
        logger.info("Modificando contrato con id: {}", contrato.getId());
        
        Contrato contratoExistente = obtenerContratoPorId(contrato.getId());
        
        // Verificar si se cambió la unidad
        if (hayCambioDeUnidad(contratoExistente, contrato)) {
            manejarCambioDeUnidad(contratoExistente, contrato);
        }

        Contrato contratoActualizado = contratoRepo.save(contrato);
        logger.info("Contrato modificado exitosamente con id: {}", contratoActualizado.getId());
        return contratoActualizado;
    }

    @Override
    public Page<Contrato> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable) {
        logger.info("Filtrando contratos para casa ID: {} con filtro: '{}' y paginación", casaId, filtro);
        Page<Contrato> page;
        
        // Validar si el filtro es válido para búsqueda
        if (filtro == null || filtro.trim().isEmpty() || isInvalidSearchTerm(filtro.trim())) {
            // Sin filtro válido, devolver todos los contratos de la casa usando método JPA automático
            logger.info("Filtro no válido o vacío, devolviendo todos los contratos de la casa {}", casaId);
            page = contratoRepo.findByUnidadCasaIdOrderByFechaInicioDesc(casaId, pageable);
        } else {
            // Con filtro válido, usar búsqueda compleja con OR entre múltiples campos
            String filtroLimpio = filtro.trim();
            page = contratoRepo.findByCasaIdWithMultiFieldFilter(casaId, filtroLimpio, pageable);
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

    @Override
    public List<Contrato> listarActivosPorCasa(Integer casaId) {
        logger.info("Listando TODOS los contratos activos para casa ID: {}", casaId);
        List<Contrato> contratos = contratoRepo.findByUnidadCasaIdAndActivoTrueOrderByFechaInicioDesc(casaId);
        logger.info("Se obtuvieron {} contratos activos para casa {}", contratos.size(), casaId);
        return contratos;
    }

    @Override
    public boolean cambiarEstado(Integer id, boolean activo) {
        logger.info("Cambiando estado de contrato con id: {} a {}", id, activo ? "activo" : "inactivo");
        
        try {
            Contrato contrato = obtenerContratoPorId(id);
            
            if (activo) {
                activarContrato(contrato);
            } else {
                desactivarContrato(contrato);
            }
            
            contratoRepo.save(contrato);
            logger.info("Estado de contrato con id: {} cambiado exitosamente a {}", id, activo ? "activo" : "inactivo");
            return true;
            
        } catch (Exception e) {
            logger.error("Error al cambiar estado del contrato con id: {}", id, e);
            return false;
        }
    }
    
    // ===============================
    // MÉTODOS AUXILIARES PRIVADOS
    // ===============================
    
    /**
     * Obtiene una unidad por ID con manejo de errores
     */
    private UnidadHabitacional obtenerUnidadPorId(Integer unidadId) {
        return unidadHabitacionalRepo.findById(unidadId)
            .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + unidadId));
    }
    
    /**
     * Obtiene un contrato por ID con manejo de errores
     */
    private Contrato obtenerContratoPorId(Integer contratoId) {
        return contratoRepo.findById(contratoId)
            .orElseThrow(() -> new RuntimeException("Contrato no encontrado con ID: " + contratoId));
    }
    
    /**
     * Valida que una unidad esté disponible para ser asignada
     */
    private void validarUnidadDisponible(UnidadHabitacional unidad) {
        if (!ESTADO_DISPONIBLE.equals(unidad.getEstado())) {
            throw new RuntimeException("La unidad " + unidad.getId() + " no está disponible. Estado actual: " + unidad.getEstado());
        }
    }
    
    /**
     * Actualiza el estado de una unidad y la guarda
     */
    private void actualizarEstadoUnidad(UnidadHabitacional unidad, String nuevoEstado) {
        logger.debug("Cambiando estado de unidad {} de '{}' a '{}'", unidad.getId(), unidad.getEstado(), nuevoEstado);
        unidad.setEstado(nuevoEstado);
        unidadHabitacionalRepo.save(unidad);
    }
    
    /**
     * Verifica si hay cambio de unidad entre contrato existente y nuevo
     */
    private boolean hayCambioDeUnidad(Contrato contratoExistente, Contrato contratoNuevo) {
        return !Objects.equals(contratoExistente.getUnidad().getId(), contratoNuevo.getUnidad().getId());
    }
    
    /**
     * Maneja el cambio de unidad en una modificación de contrato
     */
    private void manejarCambioDeUnidad(Contrato contratoExistente, Contrato contratoNuevo) {
        // Liberar unidad anterior
        actualizarEstadoUnidad(contratoExistente.getUnidad(), ESTADO_DISPONIBLE);
        
        // Validar y ocupar nueva unidad
        UnidadHabitacional nuevaUnidad = obtenerUnidadPorId(contratoNuevo.getUnidad().getId());
        validarUnidadDisponible(nuevaUnidad);
        actualizarEstadoUnidad(nuevaUnidad, ESTADO_OCUPADO);
        
        logger.info("Cambio de unidad procesado: {} -> {}", 
            contratoExistente.getUnidad().getId(), contratoNuevo.getUnidad().getId());
    }
    
    /**
     * Activa un contrato y marca su unidad como ocupada
     */
    private void activarContrato(Contrato contrato) {
        UnidadHabitacional unidad = obtenerUnidadPorId(contrato.getUnidad().getId());
        validarUnidadDisponible(unidad);
        
        contrato.setActivo(true);
        actualizarEstadoUnidad(unidad, ESTADO_OCUPADO);
        logger.info("Contrato activado y unidad {} marcada como ocupada", unidad.getId());
    }
    
    /**
     * Desactiva un contrato y marca su unidad como disponible
     */
    private void desactivarContrato(Contrato contrato) {
        contrato.setActivo(false);
        actualizarEstadoUnidad(contrato.getUnidad(), ESTADO_DISPONIBLE);
        logger.info("Contrato desactivado y unidad {} marcada como disponible", contrato.getUnidad().getId());
    }
}
