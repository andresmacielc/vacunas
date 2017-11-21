package com.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "vacunas", schema = "public")
public class Vacunas implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	@SequenceGenerator(name = "VACUNAS_GENERATOR", allocationSize=1, initialValue=1, sequenceName = "public.vacunas_seq")
	@GeneratedValue(generator = "VACUNAS_GENERATOR", strategy=GenerationType.SEQUENCE)
	@Id
	@Column(name = "id_vacuna")
	private Long idVacuna;

	@Column(name = "id_hijo")
	private Long idHijo;

	@Column(name = "nombre_vacuna")
	private String nombreVacuna;

	@Column(name = "fecha_aplicacion")
	@Temporal(TemporalType.DATE)
	private Date fechaAplicacion;

	@Column(name = "aplicada")
	private Boolean aplicada;

	@Column(name = "notificado")
	private Boolean notificado;

	public Long getIdVacuna() {
		return idVacuna;
	}

	public void setIdVacuna(Long idVacuna) {
		this.idVacuna = idVacuna;
	}

	public Long getIdHijo() {
		return idHijo;
	}

	public void setIdHijo(Long idHijo) {
		this.idHijo = idHijo;
	}

	public String getNombreVacuna() {
		return nombreVacuna;
	}

	public void setNombreVacuna(String nombreVacuna) {
		this.nombreVacuna = nombreVacuna;
	}

	public Date getFechaAplicacion() {
		return fechaAplicacion;
	}

	public void setFechaAplicacion(Date fechaAplicacion) {
		this.fechaAplicacion = fechaAplicacion;
	}

	public Boolean getAplicada() {
		return aplicada;
	}

	public void setAplicada(Boolean aplicada) {
		this.aplicada = aplicada;
	}

	public Boolean getNotificado() {
		return notificado;
	}

	public void setNotificado(Boolean notificado) {
		this.notificado = notificado;
	}

}
