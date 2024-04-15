
# Ejemplo 11 - Ejemplo una asociación _Many To Many_ con atributos extra

Partimos desde el ejemplo [many-to-many](../ManyToMany/) con algunas modificaciones:

- Eliminamos la asociación `@ManyToMany` de ambos lados, tanto de `Asignatura` como de `Alumno`, ya que vamos a darle solución de otra forma.
- Transformamos de `List` a `Set` el tipo de dato de las dos colecciones de las asociaciones `@OneToMany` en curso, ya que si no obtenemos una excepción de tipo  `MultipleFetchBagException`. El próximo curso trabajaremos una solución más adecuada que esta, pero por ahora nos sirve.

## ¿Cuál es nuestro modelo de datos?

![diagrama uml](./uml.jpg) 

En este caso, podemos apreciar una clase de asociación muchos a muchos, que nos permite reflejar que un alumno se matricula en muchas asignaturas, así como sus notas.

## Asociación `@ManyToMany` con atributos extra

La implementación de una asociación _muchos-a-muchos_ simple ha resultado ser muy sencilla (incluso en el tratamiento bidireccional). Sin embargo, para **añadir atributos extra**, necesitamos crear una nueva entidad, y hacer uso de un identificador compuesto, que en este caso hará uso de `@MapsId`.

## Implementación de la solución

### ¿Qué tenemos hasta ahora?

- Entidad `Alumno` con su repositorio y servicio
- Entidad `Curso` con su repositorio y servicio
- Entidad `Asignatura`, con su repositorio y servicio

También las asociaciones entre `Alumno` y `Curso` y entre `Curso` y `Asignatura`.

### Paso 1: Creamos la nueva clase que va a _mapear_ el identificador de la asociación _muchos-a-muchos_.

```java
@Data @NoArgsConstructor
@Embeddable
public class NotasPK implements Serializable{
    
    private static final long serialVersionUID = 1L;
	
	private long alumno_id;
	private long asignatura_id;

}
```

Como podemos comprobar, cumplimos con las reglas estipuladas por JPA:

- Clase pública
- Implementa serializable
- Gracias a Lombok, tenemos un constructor público sin argumentos, y la implementación de `equals` y `hashCode`.

### Paso 2: Creamos la nuevo entidad que va a _mapear_ la asociación _muchos-a-muchos_.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notas {
	/*
	 * Así no nos vale, no es suficiente
	 * 
	 * @Id
	 * 
	 * @GeneratedValue private Long id;
	 */
	@EmbeddedId
	private NotasPK notasPK = new NotasPK();
	
	
	public Notas(Alumno a, Asignatura as) {
		this.alumno = a;
		this.asignatura = as;
	}

	@ManyToOne
	@MapsId("alumno_id")
	@JoinColumn(name = "alumno_id")
	private Alumno alumno;

	@ManyToOne
	@MapsId("asignatura_id")
	@JoinColumn(name = "asignatura_id")
	private Asignatura asignatura;

	private int primeraEv, segundaEv, terceraEv, notaFinal;

}

```

Como podemos observar, vamos a **romper nuestra asociación `@ManyToMany` para utilizar dos conjuntos de asociaciónes  `@ManyToOne` + `@OneToMany`.** En esta entidad tenemos las asociaciones `@ManyToOne`.

> Lo único obligatorio al romper la asociación _many-to-many_ es que tengamos dos asociaciones _many-to-one_. Ahora bien, si nos interesa, cada una de ellas puede ser también bidireccional, con el lado _one-to-many_ correspondiente.

Como hemos visto anteriormente en la teoría, podemos destacar dos cosas:

- Usamos `@EmbeddedId` para marcar la clave primaria (en lugar de usar `@Id`, como veníamos haciendo hasta ahora`).
- Marcamos los campos de tipo `Alumno` y `Asignatura` con `@MapsId`. Con esto conseguimos vincular cada campo con una parte de la clave primaria, y son las claves externas de una asociación _muchos-a-uno_.


### Paso 3: Modificación de las clases `Alumno` y/o `Asignatura`.

Ahora, podemos modificar ambas clases para establecer la asociación de forma bidireccional. **Solamente lo hacemos desde el lado de Alumno**.

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alumno {
	
	// Resto de atributos
	
	
	@OneToMany(mappedBy="alumno", fetch = FetchType.EAGER)
	@Builder.Default
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Notas> notas = new ArrayList<>();
	
	// Resto de métodos

}
```

Esto nos obliga a, en la entidad `Notas`, añadir los métodos _helper_:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notas {
	
    // Resto de código

	/*
	 * MÉTODOS HELPER
	 */

	public void addToAlumno(Alumno a) {
		a.getNotas().add(this);
		this.alumno = a;
	}

	public void removeFromAlumno(Alumno a) {
		a.getNotas().remove(this);
		this.alumno = null;
	}

}

```

### Paso 4: Creación del repositorio y servicios para la clase `Notas`. Utilización de la nueva clase de asociación.

El repositorio:

```java
public interface NotasRepositorio 
	extends JpaRepository<Notas, NotasPK>{

}
```

y el servicio

```java
@Service
public class MatriculaNotasServicio 
	extends BaseServiceImpl<Notas, NotasPK, NotasRepositorio>{

    }
```

> El servicio se basa en el ejemplo de [Servicio Base](../ServicioBase/).

### Paso 5: Lógica de negocio

### Algo de teoría

Según la wikipedia:

> Las Reglas del Negocio o Conjunto de Reglas de Negocio describe las políticas, normas, operaciones, definiciones y restricciones presentes en una organización y que son de vital importancia para alcanzar los objetivos misionales.

Es decir, se trata de la implementación de aquellos procesos, flujos de información, reglas de validación, ... propias del sistema que estamos implementando, y que no tienen por qué ser las mismas en otro sistema.

Es decir, que por un lado tendríamos las operaciones de gestión de cada entidad (insertar, eliminar, actualizar, consultar), y por otro lado, tendríamos la lógica de negocio. Esta última puede _utilizar varios CRUD para realizar su misión_.

### Ejemplo

Continuemos con nuestro ejemplo de un colegio. Uno de los procesos propios que definen un colegio es la matriculación de los alumnos. Esto, por ejemplo, podría incluir los siguientes pasos:

1. Insertar al alumno.
2. Asociarlo al curso al que se matricula.
3. Asociarlo a todas las asignaturas de ese curso (matrícula completa)
4. Enviar un email de confirmación.

La implementación de estos pasos en **uno o más servicios** sería la lógica de negocio de nuestra aplicación.

### Código

En el caso del servicio, podemos aprovechar para implementar algún método de la lógica de negocio, como la matriculación de un alumno en todas las asignaturas de un curso


```java
@Service
@RequiredArgsConstructor
public class MatriculaNotasServicio 
	extends BaseServiceImpl<Notas, NotasPK, NotasRepositorio>{

	private final AlumnoServicio alumnoServicio;
	
	// Método que matricula a un alumno en todas
	// las asignaturas de un curso.
	public Alumno matriculaCurso(Alumno a, Curso c) {
		a.addToCurso(c);
		alumnoServicio.save(a);
		
		for (Asignatura asig : c.getAsignaturas()) {
			Notas n = new Notas(a, asig);
			n.addToAlumno(a);
			this.save(n);
		}
		
		return a;
		
	}
	
	
}
```

### Paso 6: Tratamiento bidireccional y no olvidar evitar referencias circulares

Como hemos comprobado, hemos pasado **de una asociación `@ManyToMany` bidireccional** a **una o asociaciones `@ManyToOne` + `@OneToMany`**. Por tanto, sería necesario excluir las listas en los métodos `equals`, `hashCode` y `toString` para evitar referencias circulares.