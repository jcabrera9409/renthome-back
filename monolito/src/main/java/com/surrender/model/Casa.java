package com.surrender.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblCasa")
public class Casa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(nullable = false)
	private String direccion;
	
	@JsonIgnore
	@OneToMany(mappedBy = "casa")
	private List<UnidadHabitacional> unidades;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "casas")
	private List<Usuario> usuarios;

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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<UnidadHabitacional> getUnidades() {
		return unidades != null ? new ArrayList<>(unidades) : new ArrayList<>();
	}

	public void setUnidades(List<UnidadHabitacional> unidades) {
		this.unidades = unidades != null ? new ArrayList<>(unidades) : new ArrayList<>();
	}

	public List<Usuario> getUsuarios() {
		return usuarios != null ? new ArrayList<>(usuarios) : new ArrayList<>();
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios != null ? new ArrayList<>(usuarios) : new ArrayList<>();
	}
}
