package org.renthome.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_tarifa_servicio")
public class TarifaServicio extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, name = "tipo_servicio")
    public String tipoServicio;

    @Column(nullable = false, name = "rango_inicio")
    public float rangoInicio;

    @Column(nullable = false, name = "rango_fin")
    public float rangoFin;

    @Column(nullable = false, name = "unidad")
    public String unidad;

    @Column(nullable = false, name = "precio_unidad")
    public float precioUnidad;

    @ManyToOne
    public Casa casa;

    // Métodos de consulta usando Panache
    public static List<TarifaServicio> findByTipoServicio(String tipoServicio) {
        return list("tipoServicio", tipoServicio);
    }

    public static List<TarifaServicio> findByCasa(Casa casa) {
        return list("casa", casa);
    }

    public static List<TarifaServicio> findByRango(float valor) {
        return list("rangoInicio <= ?1 AND rangoFin >= ?1", valor);
    }

    // Getters y Setters para compatibilidad
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public float getRangoInicio() {
        return rangoInicio;
    }

    public void setRangoInicio(float rangoInicio) {
        this.rangoInicio = rangoInicio;
    }

    public float getRangoFin() {
        return rangoFin;
    }

    public void setRangoFin(float rangoFin) {
        this.rangoFin = rangoFin;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public float getPrecioUnidad() {
        return precioUnidad;
    }

    public void setPrecioUnidad(float precioUnidad) {
        this.precioUnidad = precioUnidad;
    }

    public Casa getCasa() {
        return casa;
    }

    public void setCasa(Casa casa) {
        this.casa = casa;
    }
}
