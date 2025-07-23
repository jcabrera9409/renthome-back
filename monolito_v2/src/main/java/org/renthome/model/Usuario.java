package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_usuario")
public class Usuario extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, name = "nombre")
    public String nombre;
    
    @Column(nullable = false, unique = true, name = "email")
    public String email;
    
    @Column(nullable = false, name = "password")
    public String password;
    
    @Column(nullable = false, name = "activo")
    public boolean activo = true;
    
    @ManyToMany
    @JoinTable(
        name = "tbl_usuario_casas",  // ← Nombre explícito
        joinColumns = @JoinColumn(name = "usuarios_id"),
        inverseJoinColumns = @JoinColumn(name = "casas_id")
    )
    public List<Casa> casas;
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Token> tokens;

    // Métodos de consulta usando Panache
    public static Usuario findByEmail(String email) {
        return find("email", email).firstResult();
    }
    
    public static List<Usuario> findActiveUsers() {
        return list("activo", true);
    }
    
    public static Usuario findByEmailAndActive(String email, boolean activo) {
        return find("email = ?1 and activo = ?2", email, activo).firstResult();
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }
}
