package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_casa")
public class Casa extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, name = "nombre")
    public String nombre;
    
    @Column(nullable = false, name = "direccion")
    public String direccion;
    
    @OneToMany(mappedBy = "casa", fetch = FetchType.LAZY)
    public List<UnidadHabitacional> unidades;
    
    @ManyToMany(mappedBy = "casas", fetch = FetchType.LAZY)
    public List<Usuario> usuarios;

    // Métodos de consulta usando Panache
    public static List<Casa> findByUsuarioEmail(String email) {
        return list("SELECT DISTINCT c FROM Casa c JOIN c.usuarios u WHERE u.email = ?1", email);
    }
    
    public static Casa findByNombre(String nombre) {
        return find("nombre", nombre).firstResult();
    }
    
    public static List<Casa> findByDireccionContaining(String direccion) {
        return list("direccion like ?1", "%" + direccion + "%");
    }
    
    @Override
    public String toString() {
        return "Casa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}
