package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_detalle_recibo")
public class DetalleRecibo extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false, name = "descripcion")
    public String descripcion;
    
    @Column(nullable = false, name = "monto")
    public float monto;
    
    @ManyToOne
    public Recibo recibo;

    // Métodos de negocio Panache
    public static java.util.List<DetalleRecibo> findByRecibo(Recibo recibo) {
        return list("recibo", recibo);
    }
    
    public static java.util.List<DetalleRecibo> findByDescripcion(String descripcion) {
        return list("descripcion", descripcion);
    }

    // Getters y Setters para compatibilidad
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public Recibo getRecibo() {
        return recibo;
    }

    public void setRecibo(Recibo recibo) {
        this.recibo = recibo;
    }
}
