package com.salesianostriana.dam.herencia.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("A")
@NoArgsConstructor
public class Admin extends Usuario {

	public Admin(Long id, String fullName, String username, String password) {
		super(id, fullName, username, password);
	}

	
	
}
