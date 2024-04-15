package com.salesianostriana.dam.idcompuesto.idclass.asoc;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
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
	@ManyToOne
	private Subsistema subsystem;

	@Id
	private String username;
	
	private String name;

}
