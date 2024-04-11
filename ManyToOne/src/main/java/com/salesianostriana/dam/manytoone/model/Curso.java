package com.salesianostriana.dam.manytoone.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String nombre, tutor;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="curso", fetch = FetchType.EAGER)
	@Builder.Default
	private List<Alumno> alumnos = new ArrayList<>();
	

}
