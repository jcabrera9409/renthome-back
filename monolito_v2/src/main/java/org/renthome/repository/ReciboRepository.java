package org.renthome.repository;

import org.renthome.model.Recibo;
import org.renthome.model.Contrato;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReciboRepository implements PanacheRepository<Recibo> {

    /**
     * Buscar recibos por contrato
     */
    public List<Recibo> findByContrato(Contrato contrato) {
        return list("contrato", contrato);
    }

    /**
     * Buscar recibos por período
     */
    public List<Recibo> findByPeriodo(YearMonth periodo) {
        return list("periodo", periodo);
    }

    /**
     * Buscar recibos pendientes de pago
     */
    public List<Recibo> findPendientesPago() {
        return list("pagado", false);
    }

    /**
     * Buscar recibos pagados
     */
    public List<Recibo> findPagados() {
        return list("pagado", true);
    }

    /**
     * Buscar todos los recibos con paginación
     */
    public List<Recibo> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar recibo por contrato y período
     */
    public Optional<Recibo> findByContratoAndPeriodo(Contrato contrato, YearMonth periodo) {
        return find("contrato = ?1 and periodo = ?2", contrato, periodo).firstResultOptional();
    }

    /**
     * Contar recibos pendientes
     */
    public long countPendientes() {
        return count("pagado", false);
    }

    /**
     * Calcular total de montos pendientes
     */
    public Double calculateTotalPendiente() {
        return find("SELECT SUM(r.montoTotal) FROM Recibo r WHERE r.pagado = false")
            .project(Double.class)
            .singleResult();
    }
}
