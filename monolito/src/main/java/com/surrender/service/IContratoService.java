package com.surrender.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.Contrato;

public interface IContratoService extends ICRUD<Contrato, Integer> {
    
    /**
     * Filtra los contratos de una casa específica con paginación
     * @param casaId ID de la casa
     * @param filtro Texto para filtrar por nombre de unidad, nombre de inquilino o documento
     * @param pageable Configuración de paginación
     * @return Página de contratos de la casa
     */
    Page<Contrato> filtrarPorCasa(Integer casaId, String filtro, Pageable pageable);
    
    /**
     * Obtiene TODOS los contratos activos de una casa específica (sin paginación)
     * @param casaId ID de la casa
     * @return Lista completa de contratos activos de la casa
     */
    List<Contrato> listarActivosPorCasa(Integer casaId);
    
    /**
     * Cambia el estado activo/inactivo de un contrato
     * @param id ID del contrato a modificar
     * @param activo Nuevo estado (true = activo, false = inactivo)
     * @return true si se cambió exitosamente, false en caso contrario
     */
    boolean cambiarEstado(Integer id, boolean activo);
}
