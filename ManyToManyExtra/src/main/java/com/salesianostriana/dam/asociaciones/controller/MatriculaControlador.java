package com.salesianostriana.dam.asociaciones.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesianostriana.dam.asociaciones.model.Alumno;
import com.salesianostriana.dam.asociaciones.model.Asignatura;
import com.salesianostriana.dam.asociaciones.model.Curso;
import com.salesianostriana.dam.asociaciones.model.Notas;
import com.salesianostriana.dam.asociaciones.services.AlumnoServicio;
import com.salesianostriana.dam.asociaciones.services.CursoServicio;
import com.salesianostriana.dam.asociaciones.services.MatriculaNotasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatriculaControlador {

	private final AlumnoServicio alumnoServicio;
	private final CursoServicio cursoServicio;
	private final MatriculaNotasServicio matriculaNotasServicio;

	
	/*************************************************************
	 * 
	 * ESTE MÉTODO ES DE APLICACIÓN ÚNICAMENTE DIDÁCTICA Y 
	 * LA FORMA DE DEVOLVER LA INFORMACIÓN NO ES LA QUE SE DEBE
	 * USAR EN EL PROYECTO FINAL
	 * 
	 **************************************************************/
	
	
	@GetMapping("/matricula/curso/{id}")
	@ResponseBody
	public String matriculaCompletaCurso(@PathVariable Long id) {

		Optional<Alumno> alumnoOpt = alumnoServicio.findById(1L);
		Optional<Curso> cursoOpt = cursoServicio.findById(id);

		Alumno alumno = null;
		
		if (alumnoOpt.isPresent() && cursoOpt.isPresent()) {
			alumno = alumnoOpt.get();
			Curso curso = cursoOpt.get();

			alumno = matriculaNotasServicio.matriculaCurso(alumno, curso);

			System.out.println(alumno);
			System.out.println(alumno.getNotas());
		} else {
			System.out.println("No existe el alumno o el curso");
		}
		return alumno != null ? 
				"%s<br/>%s<br/>%s".formatted(
						alumno.toString(), 
						alumno.getCurso(), 
						alumno.getNotas().stream()
							.map(Notas::getAsignatura)
							.map(Asignatura::getNombre)
							.collect(Collectors.joining(", "))
						
						) : 
					"No existe el alumno o el curso";
	}
	
	/*************************************************************
	 * 
	 * ESTE MÉTODO ES DE APLICACIÓN ÚNICAMENTE DIDÁCTICA Y 
	 * LA FORMA DE DEVOLVER LA INFORMACIÓN NO ES LA QUE SE DEBE
	 * USAR EN EL PROYECTO FINAL
	 * 
	 **************************************************************/


}
