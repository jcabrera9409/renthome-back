package com.surrender.repo;

import java.util.List;

import com.surrender.model.TarifaServicio;

public interface TarifaServicioRepo extends IGenericRepo<TarifaServicio, Integer> {
    
    /**
     * Encuentra todas las tarifas de servicio activas
     * @return Lista de tarifas activas
     */
    List<TarifaServicio> findByActivoTrue();
}