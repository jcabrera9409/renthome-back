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
    
    @Column(nullable = false, unique = true, name = "access_token", length = 2048)
    public String accessToken;
    
    @Column(nullable = false, unique = true, name = "refresh_token", length = 2048)
    public String refreshToken;
    
    @Column(nullable = false, name = "logged_out")
    public boolean loggedOut;
    
    @ManyToOne(optional = false)
    public Usuario usuario;

    
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
