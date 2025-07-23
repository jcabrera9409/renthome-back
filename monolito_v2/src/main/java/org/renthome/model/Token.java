package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_token")
public class Token extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, unique = true, name = "access_token")
    public String accessToken;
    
    @Column(nullable = false, unique = true, name = "refresh_token")
    public String refreshToken;
    
    @Column(nullable = false, name = "logged_out")
    public boolean loggedOut;
    
    @ManyToOne(optional = false)
    public Usuario usuario;

    // Métodos de consulta usando Panache
    public static Token findByAccessToken(String accessToken) {
        return find("accessToken", accessToken).firstResult();
    }
    
    public static Token findByRefreshToken(String refreshToken) {
        return find("refreshToken", refreshToken).firstResult();
    }
    
    public static List<Token> findByUsuario(Usuario usuario) {
        return list("usuario", usuario);
    }
    
    public static void invalidateAllUserTokens(Usuario usuario) {
        update("loggedOut = true where usuario = ?1", usuario);
    }
    
    // Métodos de negocio
    public boolean isValid() {
        return !loggedOut;
    }
    
    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", loggedOut=" + loggedOut +
                ", usuario=" + (usuario != null ? usuario.email : "null") +
                '}';
    }
}
