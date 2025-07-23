package org.renthome.repository;

import org.renthome.model.Inquilino;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InquilinoRepository implements PanacheRepository<Inquilino> {

    /**
     * Buscar todos los inquilinos con paginación
     */
    public List<Inquilino> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar inquilino por nombre completo
     */
    public Optional<Inquilino> findByNombreCompleto(String nombreCompleto) {
        return find("nombreCompleto", nombreCompleto).firstResultOptional();
    }

    /**
     * Buscar inquilinos por nombre que contenga texto
     */
    public List<Inquilino> findByNombreCompletoContaining(String nombre) {
        return list("nombreCompleto like ?1", "%" + nombre + "%");
    }

    /**
     * Buscar inquilinos por email
     */
    public Optional<Inquilino> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Buscar inquilinos por número de teléfono
     */
    public Optional<Inquilino> findByTelefono(String telefono) {
        return find("telefono", telefono).firstResultOptional();
    }

    /**
     * Verificar si existe un inquilino con el email dado
     */
    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }

    /**
     * Contar total de inquilinos
     */
    public long countAll() {
        return count();
    }
}
