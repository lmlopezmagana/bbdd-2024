package com.salesianostriana.dam.herencia.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class ClienteVip extends Cliente {
	
	private LocalDate fechaVip;

	public ClienteVip(Long id, String email, String nombre, String apellidos, LocalDate fechaVip) {
		super(id, email, nombre, apellidos);
		this.fechaVip = fechaVip;
	}
	
	

}
