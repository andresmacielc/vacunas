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

@Entity
@Table(name = "hijos", schema = "public")
public class Hijos implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	@SequenceGenerator(name = "HIJOS_GENERATOR", allocationSize=1, initialValue=1, sequenceName = "public.hijos_seq")
	@GeneratedValue(generator = "HIJOS_GENERATOR", strategy=GenerationType.SEQUENCE)
	@Id
	@Column(name = "id_hijo")
	private Long idHijo;

	@Column(name = "id_padre")
	private Long idPadre;

	@Column(name = "nombres")
	private String nombres;

	@Column(name = "apellidos")
	private String apellidos;

	@Column(name = "edad")
	private Long Edad;

	@Column(name = "documento")
	private String documento;

	@Column(name = "fecha_nacimiento")
	private Date fechaNacimiento;

	@Column(name = "sexo")
	private String sexo;

	public Long getIdHijo() {
		return idHijo;
	}

	public void setIdHijo(Long idHijo) {
		this.idHijo = idHijo;
	}

	public Long getIdPadre() {
		return idPadre;
	}

	public void setIdPadre(Long idPadre) {
		this.idPadre = idPadre;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Long getEdad() {
		return Edad;
	}

	public void setEdad(Long edad) {
		Edad = edad;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	

}
