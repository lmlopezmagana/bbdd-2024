package com.salesianostriana.dam.idcompuesto.idclass.asoc;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UsuarioPk implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Subsistema subsystem;

	private String username;

}
