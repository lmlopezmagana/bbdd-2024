package com.salesianostriana.dam.idcompuesto.idclass.asoc;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subsistema {
	
	@Id
	private String id;

	private String description;

}
