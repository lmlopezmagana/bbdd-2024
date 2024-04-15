package com.salesianostriana.dam.idcompuesto.embeddid;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UsuarioPk implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String subsystem;

	private String username;

}
