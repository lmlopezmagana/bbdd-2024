package com.salesianostriana.dam;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.salesianostriana.dam.modelo.Curso;
import com.salesianostriana.dam.servicios.AlumnoServicio;
import com.salesianostriana.dam.servicios.CursoServicio;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner init(AlumnoServicio alumnoServicio, CursoServicio cursoServicio) {
		return args -> {

			System.out.println("CURSOS que contienen la palabra DAM");
			System.out.println("===================================");
			List<Curso> dams = cursoServicio.buscarPorNombre("DAM");
			dams.forEach(c -> {
				System.out.println(c.getNombre() + ": " + cursoServicio.alumnoEnUnCurso(c) + " alumnos");
			});
			
			
			System.out.println("\n\n5 alumnos apellidados Molina, ordenados por fecha de nacimiento");
			System.out.println("===================================");
			alumnoServicio.cincoPorApellido("Molina").forEach(a -> {
				System.out.println(a.getNombre() + " " + a.getApellido1() + " - " + a.getFechaNacimiento().toString());
			});
			
			
			System.out.println("\n\n3 alumnos cuyo primero o segundo apellido es Gomez");
			System.out.println("===================================");
			alumnoServicio.nombreAlumnosContieneApellido("Gomez").forEach(System.out::println);

			System.out.println("\n\nAlumnos cuyo curso contiene la palabra DAM ordenados por apellidos");
			System.out.println("===================================");
			alumnoServicio.alumnosDeUnCurso("dam")
					.forEach(a -> System.out.println("%s %s %s".formatted(a.getNombre(), a.getApellido1(), a.getApellido2())));
		};
	}

}
