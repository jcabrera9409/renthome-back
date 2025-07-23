package org.renthome.repository;

import org.renthome.model.TarifaServicio;
import org.renthome.model.Casa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TarifaServicioRepository implements PanacheRepository<TarifaServicio> {

    /**
     * Buscar tarifas por tipo de servicio
     */
    public List<TarifaServicio> findByTipoServicio(String tipoServicio) {
        return list("tipoServicio", tipoServicio);
    }

    /**
     * Buscar tarifas por casa
     */
    public List<TarifaServicio> findByCasa(Casa casa) {
        return list("casa", casa);
    }

    /**
     * Buscar tarifa por rango de valor
     */
    public List<TarifaServicio> findByRango(float valor) {
        return list("rangoInicio <= ?1 AND rangoFin >= ?1", valor);
    }

    /**
     * Buscar todas las tarifas con paginación
     */
    public List<TarifaServicio> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar tarifa específica por casa y tipo de servicio
     */
    public Optional<TarifaServicio> findByCasaAndTipoServicio(Casa casa, String tipoServicio) {
        return find("casa = ?1 and tipoServicio = ?2", casa, tipoServicio).firstResultOptional();
    }

    /**
     * Buscar tarifas por unidad
     */
    public List<TarifaServicio> findByUnidad(String unidad) {
        return list("unidad", unidad);
    }

    /**
     * Buscar tarifas ordenadas por precio
     */
    public List<TarifaServicio> findAllOrderByPrecio() {
        return list("ORDER BY precioUnidad ASC");
    }

    /**
     * Contar tarifas por casa
     */
    public long countByCasa(Casa casa) {
        return count("casa", casa);
    }
}
