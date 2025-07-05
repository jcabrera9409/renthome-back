package com.surrender.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "tblRecibo")
public class Recibo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private YearMonth periodo;
	
	@Column(nullable = false)
	private float montoTotal;
	
	@Column(nullable = false)
	private boolean pagado;
	
	@ManyToOne
	private Contrato contrato;
	
	@OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL)
	private List<DetalleRecibo> detalle;

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
