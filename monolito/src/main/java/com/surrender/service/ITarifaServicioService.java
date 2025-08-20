package com.surrender.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.TarifaServicio;

public interface ITarifaServicioService extends ICRUD<TarifaServicio, Integer> {
    
    /**
     * Lista todas las tarifas de servicio activas
     * @return Lista de tarifas activas
     */
    List<TarifaServicio> listarActivos();
    
    /**
     * Cambia el estado de una tarifa de servicio (activo/inactivo)
     * @param id ID de la tarifa
     * @param activo Nuevo estado
     * @return true si se cambió exitosamente, false en caso contrario
     */
    boolean cambiarEstado(Integer id, boolean activo);
    
    /**
     * Lista todas las tarifas de servicio de una casa específica
     * @param casaId ID de la casa
     * @return Lista de tarifas de la casa
     */
    List<TarifaServicio> listarPorCasa(Integer casaId);
    
    /**
     * Lista las tarifas de servicio activas de una casa específica
     * @param casaId ID de la casa
     * @return Lista de tarifas activas de la casa
     */
    List<TarifaServicio> listarActivosPorCasa(Integer casaId);
    
    /**
     * Filtra las tarifas de servicio de una casa específica con paginación
     * @param casaId ID de la casa
     * @param filtro Texto para filtrar por tipo de servicio o unidad
     * @param pageable Configuración de paginación
     * @return Página de tarifas de la casa
     */
    Page<TarifaServicio> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable);
}
