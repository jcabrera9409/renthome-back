package org.renthome.repository;

import org.renthome.model.Token;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TokenRepository implements PanacheRepository<Token> {

    /**
     * Buscar token por access token
     */
    public Optional<Token> findByAccessToken(String accessToken) {
        return find("accessToken", accessToken).firstResultOptional();
    }

    /**
     * Buscar token por refresh token
     */
    public Optional<Token> findByRefreshToken(String refreshToken) {
        return find("refreshToken", refreshToken).firstResultOptional();
    }

    /**
     * Buscar tokens activos de un usuario
     */
    public List<Token> findByUsuarioIdAndLoggedOutFalse(Integer usuarioId) {
        return list("usuario.id = ?1 and loggedOut = false", usuarioId);
    }

    /**
     * Buscar todos los tokens con paginación
     */
    public List<Token> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

    /**
     * Buscar tokens expirados
     */
    public List<Token> findExpiredTokens() {
        return list("expiresAt < CURRENT_TIMESTAMP");
    }

    /**
     * Contar tokens activos de un usuario
     */
    public long countActiveTokensByUsuario(Integer usuarioId) {
        return count("usuario.id = ?1 and loggedOut = false", usuarioId);
    }
}
