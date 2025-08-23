package com.surrender.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.surrender.model.Contrato;

public interface ContratoRepo extends IGenericRepo<Contrato, Integer> {
    
    // Métodos JPA automáticos utilizados por el servicio
    
    /**
     * Obtiene todos los contratos de una casa ordenados por fecha de inicio DESC
     */
    Page<Contrato> findByUnidadCasaIdOrderByFechaInicioDesc(Integer casaId, Pageable pageable);
    
    /**
     * Obtiene todos los contratos activos de una casa ordenados por fecha de inicio DESC (con paginación)
     */
    Page<Contrato> findByUnidadCasaIdAndActivoTrueOrderByFechaInicioDesc(Integer casaId, Pageable pageable);
    
    /**
     * Obtiene TODOS los contratos activos de una casa ordenados por fecha de inicio DESC (sin paginación)
     */
    List<Contrato> findByUnidadCasaIdAndActivoTrueOrderByFechaInicioDesc(Integer casaId);
    
    // Métodos @Query para búsquedas complejas con OR entre múltiples campos
    
    /**
     * Búsqueda compleja con OR entre nombre unidad, nombre inquilino y documento
     */
    @Query("""
        SELECT c FROM Contrato c 
        JOIN c.unidad u 
        JOIN c.inquilino i 
        WHERE u.casa.id = :casaId 
        AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) 
             OR LOWER(i.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) 
             OR LOWER(i.documentoIdentidad) LIKE LOWER(CONCAT('%', :filtro, '%')))
        ORDER BY c.fechaInicio DESC
        """)
    Page<Contrato> findByCasaIdWithMultiFieldFilter(@Param("casaId") Integer casaId, @Param("filtro") String filtro, Pageable pageable);
    
    /**
     * Búsqueda compleja con OR para contratos activos entre nombre unidad, nombre inquilino y documento
     */
    @Query("""
        SELECT c FROM Contrato c 
        JOIN c.unidad u 
        JOIN c.inquilino i 
        WHERE u.casa.id = :casaId 
        AND c.activo = true 
        AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) 
             OR LOWER(i.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) 
             OR LOWER(i.documentoIdentidad) LIKE LOWER(CONCAT('%', :filtro, '%')))
        ORDER BY c.fechaInicio DESC
        """)
    Page<Contrato> findActiveByCasaIdWithMultiFieldFilter(@Param("casaId") Integer casaId, @Param("filtro") String filtro, Pageable pageable);
}
