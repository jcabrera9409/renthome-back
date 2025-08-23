package com.surrender.repo;

import java.util.List;

import com.surrender.model.Inquilino;

public interface InquilinoRepo extends IGenericRepo<Inquilino, Integer> {
    
    /**
     * Obtiene todos los inquilinos activos ordenados por nombre completo
     */
    List<Inquilino> findByActivoTrueOrderByNombreCompletoAsc();
}
