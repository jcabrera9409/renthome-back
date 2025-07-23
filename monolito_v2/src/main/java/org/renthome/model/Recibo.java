package org.renthome.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_recibo")
public class Recibo extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    @Column(nullable = false)
    public YearMonth periodo;
    
    @Column(nullable = false, name = "monto_total")
    public float montoTotal;
    
    @Column(nullable = false, name = "pagado")
    public boolean pagado;
    
    @ManyToOne
    public Contrato contrato;
    
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL)
    public List<DetalleRecibo> detalle;

    // Getters y Setters para compatibilidad
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }

    public float getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(float montoTotal) {
        this.montoTotal = montoTotal;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public List<DetalleRecibo> getDetalle() {
        return detalle != null ? new ArrayList<>(detalle) : new ArrayList<>();
    }

    public void setDetalle(List<DetalleRecibo> detalle) {
        this.detalle = detalle != null ? new ArrayList<>(detalle) : new ArrayList<>();
    }
}