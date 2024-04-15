package com.salesianostriana.dam.herencia.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("C")
@NoArgsConstructor
public class Cliente extends Usuario {
	
	private String telefono;
	
	public Cliente(Long id, String fullName, String username, String password) {
		super(id, fullName, username, password);
	}
	
	

}
