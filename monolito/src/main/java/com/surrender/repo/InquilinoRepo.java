package com.surrender.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.surrender.model.Inquilino;

public interface InquilinoRepo extends IGenericRepo<Inquilino, Integer> {
    
    /**
     * Obtiene todos los inquilinos activos ordenados por nombre completo
     */
    List<Inquilino> findByActivoTrueOrderByNombreCompletoAsc();
    
    /**
     * Búsqueda con OR entre nombre completo, documento, teléfono y correo usando JPA
     */
    Page<Inquilino> findByNombreCompletoContainingIgnoreCaseOrDocumentoIdentidadContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreCompletoAsc(
        String nombreCompleto, String documentoIdentidad, String telefono, String correo, Pageable pageable);
}
