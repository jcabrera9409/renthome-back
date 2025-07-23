package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_inquilino")
public class Inquilino extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, name = "nombre_completo")
    public String nombreCompleto;
    
    @Column(nullable = false, unique = true, name = "documento_identidad")
    public String documentoIdentidad;
    
    @Column(nullable = false, name = "telefono")
    public String telefono;
    
    @Column(nullable = false, unique = true, name = "correo")
    public String correo;
    
    @OneToMany(mappedBy = "inquilino", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Contrato> contratos;

    
    // Métodos de negocio
    public boolean tieneContratosActivos() {
        return contratos != null && contratos.stream()
                .anyMatch(contrato -> contrato.activo);
    }
    
    @Override
    public String toString() {
        return "Inquilino{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
