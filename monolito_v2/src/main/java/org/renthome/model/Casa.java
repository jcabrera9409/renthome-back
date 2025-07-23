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

    @Override
    public String toString() {
        return "Casa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}
