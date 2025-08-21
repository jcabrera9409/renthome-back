package com.surrender.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.UnidadHabitacional;

public interface IUnidadHabitacionalService extends ICRUD<UnidadHabitacional, Integer> {
    
    /**
     * Lista todas las unidades habitacionales de una casa específica
     * @param casaId ID de la casa
     * @return Lista de unidades de la casa
     */
    List<UnidadHabitacional> listarPorCasa(Integer casaId);
    
    /**
     * Lista las unidades habitacionales disponibles de una casa específica
     * @param casaId ID de la casa
     * @return Lista de unidades disponibles de la casa
     */
    List<UnidadHabitacional> listarDisponiblesPorCasa(Integer casaId);
    
    /**
     * Filtra las unidades habitacionales de una casa específica con paginación
     * @param casaId ID de la casa
     * @param filtro Texto para filtrar por nombre, tipo de unidad o estado
     * @param pageable Configuración de paginación
     * @return Página de unidades de la casa
     */
    Page<UnidadHabitacional> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable);
    
    /**
     * Cambia el estado de una unidad habitacional
     * @param id ID de la unidad
     * @param estado Nuevo estado (ej: "Disponible", "Ocupada", "Mantenimiento")
     * @return true si se cambió exitosamente, false en caso contrario
     */
    boolean cambiarEstado(Integer id, String estado);
}
