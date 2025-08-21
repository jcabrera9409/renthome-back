package com.surrender.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblUnidadHabitacional")
public class UnidadHabitacional {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false, columnDefinition="TEXT")
	private String descripcion;

	@Column(nullable = false)
	private boolean incluyeAgua;
	
	@Column(nullable = false)
	private boolean incluyeLuz;
	
	@Column(nullable = false)
	private String tipoUnidad;
	
	@Column(nullable = false)
	private String estado;
	
	@ManyToOne
	private Casa casa;
	
	@JsonIgnore
	@OneToMany(mappedBy = "unidad")
	private List<Contrato> contratos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isIncluyeAgua() {
		return incluyeAgua;
	}

	public void setIncluyeAgua(boolean incluyeAgua) {
		this.incluyeAgua = incluyeAgua;
	}

	public boolean isIncluyeLuz() {
		return incluyeLuz;
	}

	public void setIncluyeLuz(boolean incluyeLuz) {
		this.incluyeLuz = incluyeLuz;
	}

	public String getTipoUnidad() {
		return tipoUnidad;
	}

	public void setTipoUnidad(String tipoUnidad) {
		this.tipoUnidad = tipoUnidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Casa getCasa() {
		return casa;
	}

	public void setCasa(Casa casa) {
		this.casa = casa;
	}

	public List<Contrato> getContratos() {
		return contratos != null ? new ArrayList<>(contratos) : new ArrayList<>();
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos != null ? new ArrayList<>(contratos) : new ArrayList<>();
	}	
}
