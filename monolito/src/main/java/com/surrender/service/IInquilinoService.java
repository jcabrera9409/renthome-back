package com.surrender.service;

import java.util.List;

import com.surrender.model.Inquilino;

public interface IInquilinoService extends ICRUD<Inquilino, Integer> {
    
    /**
     * Lista todos los inquilinos disponibles (activos)
     * @return Lista de inquilinos activos
     */
    List<Inquilino> listarDisponibles();
}
