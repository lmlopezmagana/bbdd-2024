package com.salesianostriana.dam.asociaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignatura {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String nombre, profesor;
	
	@ManyToOne
	private Curso curso;
	
	
	
	
	/**
	 * MÉTODOS HELPER
	 */
	
	public void addToCurso(Curso c) {
		c.getAsignaturas().add(this);
		this.curso = c;
		
	}
	
	public void removeFromCurso(Curso c) {
		c.getAsignaturas().remove(this);
		this.curso = null;
	}

}
