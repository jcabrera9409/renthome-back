package com.surrender.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.TarifaServicio;

public interface TarifaServicioRepo extends IGenericRepo<TarifaServicio, Integer> {
    
    /**
     * Encuentra todas las tarifas de servicio activas
     * @return Lista de tarifas activas
     */
    List<TarifaServicio> findByActivoTrue();
    
    /**
     * Encuentra todas las tarifas de servicio de una casa específica
     * @param casaId ID de la casa
     * @return Lista de tarifas de la casa
     */
    List<TarifaServicio> findByCasaId(Integer casaId);
    
    /**
     * Encuentra las tarifas de servicio activas de una casa específica
     * @param casaId ID de la casa
     * @return Lista de tarifas activas de la casa
     */
    List<TarifaServicio> findByCasaIdAndActivoTrue(Integer casaId);
    
    /**
     * Encuentra las tarifas de servicio de una casa específica con paginación
     * @param casaId ID de la casa
     * @param pageable Configuración de paginación
     * @return Página de tarifas de la casa
     */
    Page<TarifaServicio> findByCasaId(Integer casaId, Pageable pageable);
    
    /**
     * Encuentra las tarifas de servicio de una casa específica filtrando por tipo de servicio o unidad
     * @param casaId ID de la casa
     * @param tipoServicio Texto a buscar en tipo de servicio (case insensitive)
     * @param unidad Texto a buscar en unidad (case insensitive)
     * @param pageable Configuración de paginación
     * @return Página de tarifas filtradas de la casa
     */
    Page<TarifaServicio> findByCasaIdAndTipoServicioContainingIgnoreCaseOrUnidadContainingIgnoreCase(
        Integer casaId, String tipoServicio, String unidad, Pageable pageable);
}