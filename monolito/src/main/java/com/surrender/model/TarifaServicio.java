package com.surrender.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblTarifaServicio")
public class TarifaServicio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String tipoServicio;
	
	@Column(nullable = false)
	private float rangoInicio;
	
	@Column(nullable = false)
	private float rangoFin;
	
	@Column(nullable = false)
	private String unidad;
	
	@Column(nullable = false)
	private float precioUnidad;
	
	@Column(nullable = false)
	private boolean activo;
	
	@ManyToOne
	private Casa casa;

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

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Casa getCasa() {
		return casa;
	}

	public void setCasa(Casa casa) {
		this.casa = casa;
	}
}
