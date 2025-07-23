package org.renthome.repository;

import org.renthome.model.DetalleRecibo;
import org.renthome.model.Recibo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DetalleReciboRepository implements PanacheRepository<DetalleRecibo> {

    /**
     * Buscar detalles por recibo
     */
    public List<DetalleRecibo> findByRecibo(Recibo recibo) {
        return list("recibo", recibo);
    }

    /**
     * Buscar detalles por descripción
     */
    public List<DetalleRecibo> findByDescripcion(String descripcion) {
        return list("descripcion", descripcion);
    }

    /**
     * Buscar todos los detalles con paginación
     */
    public List<DetalleRecibo> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Calcular total por recibo
     */
    public Double calculateTotalByRecibo(Recibo recibo) {
        return find("SELECT SUM(d.monto) FROM DetalleRecibo d WHERE d.recibo = ?1", recibo)
            .project(Double.class)
            .singleResult();
    }

    /**
     * Contar detalles por recibo
     */
    public long countByRecibo(Recibo recibo) {
        return count("recibo", recibo);
    }
}
