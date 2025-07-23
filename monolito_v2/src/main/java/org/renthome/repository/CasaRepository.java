package org.renthome.repository;

import org.renthome.model.Casa;
import org.renthome.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CasaRepository implements PanacheRepository<Casa> {

    /**
     * Buscar casas por email de usuario (migrado desde Casa.findByUsuarioEmail)
     */
    public List<Casa> findByUsuarioEmail(String email) {
        return list("SELECT DISTINCT c FROM Casa c JOIN c.usuarios u WHERE u.email = ?1", email);
    }

    /**
     * Buscar casa por nombre (migrado desde Casa.findByNombre)
     */
    public Optional<Casa> findByNombre(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }

    /**
     * Buscar casas por dirección que contenga texto (migrado desde Casa.findByDireccionContaining)
     */
    public List<Casa> findByDireccionContaining(String direccion) {
        return list("direccion like ?1", "%" + direccion + "%");
    }

    /**
     * Buscar todas las casas con paginación
     */
    public List<Casa> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar casas de un usuario específico
     */
    public List<Casa> findByUsuario(Usuario usuario) {
        return list("SELECT c FROM Casa c JOIN c.usuarios u WHERE u = ?1", usuario);
    }

    /**
     * Contar casas por usuario
     */
    public long countByUsuario(Usuario usuario) {
        return count("SELECT COUNT(c) FROM Casa c JOIN c.usuarios u WHERE u = ?1", usuario);
    }

    /**
     * Verificar si existe una casa con el nombre dado
     */
    public boolean existsByNombre(String nombre) {
        return find("nombre", nombre).count() > 0;
    }
}
