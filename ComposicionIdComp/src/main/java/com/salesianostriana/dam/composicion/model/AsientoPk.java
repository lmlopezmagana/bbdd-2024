package com.salesianostriana.dam.composicion.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoPk implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	private Avion avion;
    private Long id;

}
