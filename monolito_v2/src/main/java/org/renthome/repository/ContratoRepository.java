package org.renthome.repository;

import org.renthome.model.Contrato;
import org.renthome.model.Inquilino;
import org.renthome.model.UnidadHabitacional;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ContratoRepository implements PanacheRepository<Contrato> {

    /**
     * Buscar contratos activos
     */
    public List<Contrato> findActiveContracts() {
        return list("activo", true);
    }

    /**
     * Buscar contratos por inquilino
     */
    public List<Contrato> findByInquilino(Inquilino inquilino) {
        return list("inquilino", inquilino);
    }

    /**
     * Buscar contratos por unidad habitacional
     */
    public List<Contrato> findByUnidadHabitacional(UnidadHabitacional unidad) {
        return list("unidadHabitacional", unidad);
    }

    /**
     * Buscar todos los contratos con paginación
     */
    public List<Contrato> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar contrato activo por unidad habitacional
     */
    public Optional<Contrato> findActiveByUnidadHabitacional(UnidadHabitacional unidad) {
        return find("unidadHabitacional = ?1 and activo = true", unidad).firstResultOptional();
    }

    /**
     * Contar contratos activos
     */
    public long countActiveContracts() {
        return count("activo", true);
    }

    /**
     * Buscar contratos por rango de monto
     */
    public List<Contrato> findByMontoRange(float minMonto, float maxMonto) {
        return list("monto >= ?1 and monto <= ?2", minMonto, maxMonto);
    }
}
