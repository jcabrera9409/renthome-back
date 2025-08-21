package com.surrender.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.UnidadHabitacional;

public interface UnidadHabitacionalRepo extends IGenericRepo<UnidadHabitacional, Integer> {
    
    /**
     * Encuentra todas las unidades habitacionales de una casa específica
     * @param casaId ID de la casa
     * @return Lista de unidades de la casa
     */
    List<UnidadHabitacional> findByCasaId(Integer casaId);
    
    /**
     * Encuentra las unidades habitacionales disponibles de una casa específica
     * @param casaId ID de la casa
     * @param estado Estado de disponibilidad (ej: "Disponible")
     * @return Lista de unidades disponibles de la casa
     */
    List<UnidadHabitacional> findByCasaIdAndEstado(Integer casaId, String estado);
    
    /**
     * Encuentra las unidades habitacionales de una casa específica con paginación
     * @param casaId ID de la casa
     * @param pageable Configuración de paginación
     * @return Página de unidades de la casa
     */
    Page<UnidadHabitacional> findByCasaId(Integer casaId, Pageable pageable);
    
    /**
     * Encuentra las unidades habitacionales de una casa específica filtrando por nombre, tipo o estado
     * @param casaId ID de la casa
     * @param nombre Texto a buscar en nombre (case insensitive)
     * @param tipoUnidad Texto a buscar en tipo de unidad (case insensitive)
     * @param estado Texto a buscar en estado (case insensitive)
     * @param pageable Configuración de paginación
     * @return Página de unidades filtradas de la casa
     */
    Page<UnidadHabitacional> findByCasaIdAndNombreContainingIgnoreCaseOrTipoUnidadContainingIgnoreCaseOrEstadoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
        Integer casaId, String nombre, String tipoUnidad, String estado, String descripcion, Pageable pageable);
}
