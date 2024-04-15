
# Ejemplo 9 - Identificador compuesto

## Algo de teoría

### Tipos `Embeddable`

En alguna ocasión, puede que interesarnos agrupar un cierto conjunto de valores: supongamos que queremos manejar la `Localizacion` de una determinada `Oficina`. Una localización está formada por una `direccion`, una `ciudad` y un `pais`. Realmente no queremos tratar una `Localizacion` como una entidad; su ciclo de vida siempre estará asociado al de la `Oficina` correspondiente. Nos puede interesar agruparlo todo, por ejemplo, para dar un tratamiento integral. 


JPA nos ofrece la anotación `@Embeddable`, que nos permite generar una clase que será _encajable_ ( _incrustable_, _embebible_) en otra entidad.

```java
@Embeddable
public class Localizacion {

	private String direccion;
	
	private String ciudad;
	
	private String pais;

	// Resto del código
}

@Entity
public class Oficina {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Localizacion localizacion;
	
	private String telefonoContacto;
	
	// Resto del código

}
```

Como podemos observar, un elemento interesante de la clase anotada con `Embeddable` es que no tiene un atributo identificador (`@Id`).

El DDL generado sería algo parecido a esto:

```sql
create table Oficina (
	id bigint not null,
	direccion varchar(255),
	ciudad varchar(255),
	pais varchar(255),
	telefonoContacto varchar(255),
	primary key (id)
)	
```

### Identificadores compuestos

Hasta ahora, todas nuestras entidades han estado identificadas mediante un _identificador_ (valga la redundancia) que ha sido simple. De hecho, siempre hemos utilizado el siguente fragmento de código:

```java
@Id
@GeneratedValue
private Long id;
```

En determinados contextos podemos necesitar otro tipo de identificador, compuesto por más de un atributo. 

JPA nos ofrece alguna estrategia para poder implementar esto, siempre y cuando se cumplan las siguientes reglas:

- El identificador compuesto debe estar representado por una _clase de clave primaria_. Esta se puede definir con las anotaciones `@EmbeddedId` o con la anotación `@IdClass`.
- La _clase de clave primaria_ debe ser pública y debe tener un constructor público sin argumentos.
- La _clase de clave primaria_ debe ser serializable.
- La _clase de clave primaria_ debe definir los métodos `equals` y `hashCode` de forma consistente.

Los atributo que forman esta composición pueden ser básicos, compuestos o anotados con `@ManyToOne`.   

Veamos algunos [ejemplos de la documentación de Hibernate](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#identifiers-composite)

#### Identificador con `@EmbeddId`

Crear un identificador con `@EmbeddId` implica que debemos crear una clase anotada con `@Embeddable` que esté compuesto por los atributos que conforman el identificador, y luego incluir un atributo de dicho tipo como identificador de la clase que tiene el id compuesto.

```java
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@EmbeddedId
	private UsuarioPk systemUserPk;
	
	private String name;

}

```

> El nombre de clase `PK` no es el más adecuado para esta clase. Se usa solo de forma ilustrativa.

#### Identificador con `@EmbeddId` con asociación `@ManyToOne` o `@OneToOne`.


JPA 2.0 agregó soporte para identificadores derivados que permiten que una entidad tome prestado el identificador de una asociación de muchos a uno o de uno a uno. La anotación `@MapsId` también puede hacer referencia a columnas de un identificador `@EmbeddedId`.

En el ejemplo de [asociación many-to-many con atributos extra](../ManyToManyExtra/) se puede ver un ejemplo de identificador derivado con asociación `@ManyToOne`.

***Con `@EmbeddedId` no podemos realizar una generación automática parcial del valor del identificador (si usamos `@GeneratedValue` no hace nada).

#### Identificador con `@IdClass`

Crear un identificador con `@IdClass` difiere del anterior en que la entidad incluye de forma individual los atributos del identificador compuesto (y no reunidos todos en una instancia del compuesto). Las instancias de la clase definida como identificador compuesto con `@IdClass` se utilizan, eminentemente, para las búsquedas por id.

```java
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

@Entity
@IdClass(UsuarioPk.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	private String subsystem;

	@Id
	private String username;
	
	private String name;

}
```

**En la entidad, todos los atributos que forman parte del identificador compuesto van anotador por `@Id`.
**

#### Identificador con `@IdClass` con asociación `@ManyToOne`

Los identificadores compuestos definidos con `@IdClass` pueden tener atributos con asociaciones mapeadas, al igual que los identificadores compuestos definidos con `@EmbeddedId`.

```java
@Entity
@IdClass(UsuarioPk.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	@ManyToOne
	private Subsistema subsystem;

	@Id
	private String username;
	
	private String name;

}


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subsistema {
	
	@Id
	private String id;

	private String description;

}


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
```

***Además, al usar `@IdClass` JPA también nos permite realizar una generación automática parcial del valor del identificador, es decir, podemos usar `@GeneratedValue`.*** Veamos un ejemplo:

```java
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

@Entity
@IdClass(UsuarioPk.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	private String subsystem;

	@Id
	private String username;

	@Id
	@GeneratedValue
	private Long registrationId;
	
	private String name;

}
```

> Esta ventaja será muy útil en algunas situaciones, como para usar un identificador compuesto en una asociación de composición entre dos entidades.

