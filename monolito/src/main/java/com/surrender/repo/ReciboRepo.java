package com.surrender.repo;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.surrender.model.Contrato;
import com.surrender.model.Recibo;

public interface ReciboRepo extends IGenericRepo<Recibo, Integer> {

    /**
     * Obtiene todos los recibos de un periodo y lista de contratos
     */
    List<Recibo> findByPeriodoAndContratoIn(YearMonth periodo, List<Contrato> contratos);

    /**
     * Verifica si existe un recibo para un periodo y contrato específicos
     */
    boolean existsByPeriodoAndContrato(YearMonth periodo, Contrato contrato);

    /**
     * Obtiene todos los recibos de una casa ordenados por período (desc) y unidad (asc)
     */
    Page<Recibo> findByContratoUnidadCasaIdOrderByPeriodoDescContratoUnidadNombreAsc(Integer casaId, Pageable pageable);

    /**
     * Filtra recibos por casa con búsqueda en unidad o inquilino
     */
    @Query("SELECT r FROM Recibo r " +
           "JOIN r.contrato c " +
           "JOIN c.unidad u " +
           "JOIN c.inquilino i " +
           "WHERE u.casa.id = :casaId " +
           "AND LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR LOWER(i.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "ORDER BY u.nombre ASC")
    Page<Recibo> findByCasaIdWithMultiFieldFilter(@Param("casaId") Integer casaId, @Param("filtro") String filtro, Pageable pageable);
}