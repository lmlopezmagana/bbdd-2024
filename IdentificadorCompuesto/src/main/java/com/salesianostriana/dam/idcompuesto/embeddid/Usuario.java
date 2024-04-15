package com.salesianostriana.dam.idcompuesto.embeddid;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@EmbeddedId
	private UsuarioPk systemUserPk;
	
	private String name;

}
