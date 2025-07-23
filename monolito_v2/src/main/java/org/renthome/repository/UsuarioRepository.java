package org.renthome.repository;

import org.renthome.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    /**
     * Buscar usuario por email
     */
    public Optional<Usuario> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Buscar todos los usuarios con paginación
     */
    public List<Usuario> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Actualizar password por ID
     */
    @Transactional
    public int updatePasswordById(Integer id, String password) {
        return update("password = :password where id = :id", 
                     Parameters.with("password", password).and("id", id));
    }

    /**
     * Buscar usuarios activos
     */
    public List<Usuario> findActiveUsers() {
        return list("activo", true);
    }

    /**
     * Verificar si existe un usuario con el email dado
     */
    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }
}
