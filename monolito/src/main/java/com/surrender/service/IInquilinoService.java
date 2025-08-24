package com.surrender.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.Inquilino;

public interface IInquilinoService extends ICRUD<Inquilino, Integer> {
    
    /**
     * Lista todos los inquilinos disponibles (activos)
     * @return Lista de inquilinos activos
     */
    List<Inquilino> listarDisponibles();
    
    /**
     * Filtra inquilinos por múltiples campos con paginación
     * @param filtro Término de búsqueda para nombre completo, documento, teléfono y correo
     * @param pageable Configuración de paginación
     * @return Página de inquilinos que coinciden con el filtro
     */
    Page<Inquilino> filtrar(String filtro, Pageable pageable);
    
    /**
     * Cambia el estado activo/inactivo de un inquilino
     * @param id ID del inquilino
     * @param estado true para activar, false para desactivar
     * @return true si se cambió exitosamente, false en caso contrario
     */
    boolean cambiarEstado(Integer id, boolean estado);
}
