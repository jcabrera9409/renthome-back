package com.surrender.model;

import java.time.LocalDate;
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
@Table(name = "tblContrato")
public class Contrato {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private LocalDate fechaInicio;
	
	@Column(nullable = true)
	private LocalDate fechaFin;
	
	@Column(nullable = false)
	private float montoRentaMensual;
	
	@Column(nullable = false)
	private float garantia;
	
	@Column(nullable = false)
	private boolean activo;
	
	@ManyToOne
	private Inquilino inquilino;
	
	@ManyToOne
	private UnidadHabitacional unidad;
	
	@OneToMany(mappedBy = "contrato")
	@JsonIgnore
	private List<Recibo> recibos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public float getMontoRentaMensual() {
		return montoRentaMensual;
	}

	public void setMontoRentaMensual(float montoRentaMensual) {
		this.montoRentaMensual = montoRentaMensual;
	}

	public float getGarantia() {
		return garantia;
	}

	public void setGarantia(float garantia) {
		this.garantia = garantia;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Inquilino getInquilino() {
		return inquilino;
	}

	public void setInquilino(Inquilino inquilino) {
		this.inquilino = inquilino;
	}

	public UnidadHabitacional getUnidad() {
		return unidad;
	}

	public void setUnidadHabitacional(UnidadHabitacional unidad) {
		this.unidad = unidad;
	}

	public List<Recibo> getRecibos() {
		return recibos != null ? new ArrayList<>(recibos) : new ArrayList<>();
	}

	public void setRecibos(List<Recibo> recibos) {
		this.recibos = recibos != null ? new ArrayList<>(recibos) : new ArrayList<>();
	}
}
