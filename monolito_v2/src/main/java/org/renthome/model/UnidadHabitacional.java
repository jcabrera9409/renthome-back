package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_unidad_habitacional")
public class UnidadHabitacional extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false)
    public String nombre;
    
    @Column(nullable = false, name = "incluye_agua")
    public boolean incluyeAgua = false;
    
    @Column(nullable = false, name = "incluye_luz")
    public boolean incluyeLuz = false;
    
    @Column(nullable = false, name = "tipo_unidad")
    public String tipoUnidad; // Ej: "APARTAMENTO", "CUARTO", "CASA_COMPLETA"
    
    @Column(nullable = false, name = "estado")
    public String estado = "DISPONIBLE"; // "DISPONIBLE", "OCUPADA", "MANTENIMIENTO"
    
    @ManyToOne(fetch = FetchType.LAZY)
    public Casa casa;
    
    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY)
    public List<Contrato> contratos;

    // Métodos de consulta usando Panache
    public static List<UnidadHabitacional> findByCasa(Casa casa) {
        return list("casa", casa);
    }
    
    public static List<UnidadHabitacional> findByEstado(String estado) {
        return list("estado", estado);
    }
    
    public static List<UnidadHabitacional> findDisponibles() {
        return list("estado", "DISPONIBLE");
    }
    
    public static List<UnidadHabitacional> findByCasaAndEstado(Casa casa, String estado) {
        return list("casa = ?1 and estado = ?2", casa, estado);
    }
    
    public static List<UnidadHabitacional> findByTipoUnidad(String tipoUnidad) {
        return list("tipoUnidad", tipoUnidad);
    }
    
    public static UnidadHabitacional findByNombreAndCasa(String nombre, Casa casa) {
        return find("nombre = ?1 and casa = ?2", nombre, casa).firstResult();
    }
    
    // Métodos de negocio
    public boolean isDisponible() {
        return "DISPONIBLE".equals(estado);
    }
    
    public void marcarComoOcupada() {
        estado = "OCUPADA";
    }
    
    public void marcarComoDisponible() {
        estado = "DISPONIBLE";
    }
    
    public void marcarEnMantenimiento() {
        estado = "MANTENIMIENTO";
    }
    
    @Override
    public String toString() {
        return "UnidadHabitacional{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipoUnidad='" + tipoUnidad + '\'' +
                ", estado='" + estado + '\'' +
                ", incluyeAgua=" + incluyeAgua +
                ", incluyeLuz=" + incluyeLuz +
                ", casa=" + (casa != null ? casa.nombre : "null") +
                '}';
    }
}
