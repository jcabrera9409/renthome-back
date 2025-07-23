package org.renthome.repository;

import org.renthome.model.UnidadHabitacional;
import org.renthome.model.Casa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UnidadHabitacionalRepository implements PanacheRepository<UnidadHabitacional> {

    /**
     * Buscar unidades por casa
     */
    public List<UnidadHabitacional> findByCasa(Casa casa) {
        return list("casa", casa);
    }

    /**
     * Buscar unidades por estado
     */
    public List<UnidadHabitacional> findByEstado(String estado) {
        return list("estado", estado);
    }

    /**
     * Buscar unidades disponibles
     */
    public List<UnidadHabitacional> findDisponibles() {
        return list("estado", "DISPONIBLE");
    }

    /**
     * Buscar unidades por casa y estado
     */
    public List<UnidadHabitacional> findByCasaAndEstado(Casa casa, String estado) {
        return list("casa = ?1 and estado = ?2", casa, estado);
    }

    /**
     * Buscar unidades por tipo
     */
    public List<UnidadHabitacional> findByTipoUnidad(String tipoUnidad) {
        return list("tipoUnidad", tipoUnidad);
    }

    /**
     * Buscar unidad por nombre y casa
     */
    public Optional<UnidadHabitacional> findByNombreAndCasa(String nombre, Casa casa) {
        return find("nombre = ?1 and casa = ?2", nombre, casa).firstResultOptional();
    }

    /**
     * Buscar todas las unidades con paginación
     */
    public List<UnidadHabitacional> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Contar unidades por estado
     */
    public long countByEstado(String estado) {
        return count("estado", estado);
    }

    /**
     * Contar unidades disponibles
     */
    public long countDisponibles() {
        return count("estado", "DISPONIBLE");
    }
}
