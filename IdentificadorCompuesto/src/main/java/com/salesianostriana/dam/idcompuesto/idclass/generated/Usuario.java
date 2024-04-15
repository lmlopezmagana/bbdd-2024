package com.salesianostriana.dam.idcompuesto.idclass.generated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(UsuarioPk.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	private String subsystem;

	@Id
	private String username;
	
	@Id
	@GeneratedValue
	private Long registrationId;
	
	private String name;

}
