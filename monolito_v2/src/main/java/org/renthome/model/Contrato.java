package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbl_contrato")
public class Contrato extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, name = "fecha_inicio")
    public LocalDate fechaInicio;
    
    @Column(nullable = true, name = "fecha_fin")
    public LocalDate fechaFin;
    
    @Column(nullable = false, name = "monto_renta_mensual")
    public float montoRentaMensual;
    
    @Column(nullable = false, name = "garantia")
    public float garantia;
    
    @Column(nullable = false, name = "activo")
    public boolean activo;
    
    @ManyToOne
    public Inquilino inquilino;
    
    @ManyToOne
    public UnidadHabitacional unidad;

    
    // Métodos de negocio
    public void finalizar() {
        activo = false;
        fechaFin = LocalDate.now();
    }
    
    public void activar() {
        activo = true;
        fechaFin = null;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    @Override
    public String toString() {
        return "Contrato{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", montoRentaMensual=" + montoRentaMensual +
                ", garantia=" + garantia +
                ", activo=" + activo +
                ", inquilino=" + (inquilino != null ? inquilino.nombreCompleto : "null") +
                ", unidad=" + (unidad != null ? unidad.nombre : "null") +
                '}';
    }
}
