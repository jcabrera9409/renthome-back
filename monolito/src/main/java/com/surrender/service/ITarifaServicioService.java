package com.surrender.service;

import java.util.List;

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
}
