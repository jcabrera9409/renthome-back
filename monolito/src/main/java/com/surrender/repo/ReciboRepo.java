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
     * Verifica si existe un recibo para un periodo y contrato específicos
     */
    boolean existsByPeriodoAndContrato(YearMonth periodo, Contrato contrato);

    /**
     * Obtiene los periodos únicos de recibos de una casa específica ordenados de manera descendente
     */
    @Query("SELECT DISTINCT r.periodo FROM Recibo r " +
           "JOIN r.contrato c " +
           "JOIN c.unidad u " +
           "WHERE u.casa.id = :casaId " +
           "ORDER BY r.periodo DESC")
    List<YearMonth> findDistinctPeriodosByCasaId(@Param("casaId") Integer casaId);

    /**
     * Filtra recibos por casa, periodo y búsqueda en unidad o inquilino
     */
    @Query("SELECT r FROM Recibo r " +
           "JOIN r.contrato c " +
           "JOIN c.unidad u " +
           "JOIN c.inquilino i " +
           "WHERE u.casa.id = :casaId " +
           "AND r.periodo = :periodo " +
           "AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR LOWER(i.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))) " +
           "ORDER BY u.nombre ASC")
    Page<Recibo> findByCasaIdPeriodoWithMultiFieldFilter(@Param("casaId") Integer casaId, @Param("periodo") YearMonth periodo, @Param("filtro") String filtro, Pageable pageable);

    /**
     * Obtiene todos los recibos de una casa para un periodo específico ordenados por unidad
     */
    Page<Recibo> findByContratoUnidadCasaIdAndPeriodoOrderByContratoUnidadNombreAsc(Integer casaId, YearMonth periodo, Pageable pageable);
}