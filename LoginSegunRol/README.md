
# Ejemplo 14 - Redirigir a una URL concreta según el rol al realizar el login 

En uno de los [ejemplos anteriores](../SeguridadEnMemoria/), teníamos el siguiente código en la clase `SecurityConfig`:

```java
			.formLogin((loginz) -> loginz
					.loginPage("/login").permitAll());

```

Con este fragmento de código, le estamos indicando al sistema que si el login se produce con éxito, nos redirija a la página desde la cual se nos redirigió previamente al login, o en su defecto, a la raíz.

En este caso, queremos tener redirecciones diferentes para roles diferentes: el empleado, una vez logueado con éxito, accederá a la parte de administración, y el cliente, a la zona de clientes. ¿Cómo podemos conseguirlo? El método `formLogin(...)` de `HttpSecurity` recibe como argumento una clase que nos permite customizar cómo se va a realizar el login, y uno de y uno de sus métodos  es `successHandler`. Este método nos permite, en lugar de indicar una url de éxito, establecer dicha url a traves de un *manejador*. Dicho manejador debe ser una instancia de una clase que implemente la interfaz `AuthenticationSuccessHandler`. 

```java
@Component
@Log
public class RoleBasedSuccessHandler 
	implements AuthenticationSuccessHandler {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

				// Lógica de redirección	

			}


	}
```

> Las anotaciones son: `@Component` para que Spring cree un bean con este manejador, para luego inyectar la dependencia en la configuración de la seguridad; y `@Log` de lombok, para escribir fácilmente en el log si hay alguna dificultad al redirigir.

> A este método solamente llegamos una vez que el usuario se ha logueado correctamente, 
 
La sistemática para establecer la redirección de esta clase no es complicada: 
- Determinamos el rol o roles del usuario (el usuario se nos presenta aquí a través de un objeto de tipo Authentication, un interfaz que forma parte de Spring Security es que es una forma básica de acceder a los datos del usuario que se ha autenticado)
- Obtenemos el rol de más peso
- Redirigimos a la URL asociada a ese rol, o en si no hay rol o ese rol no lo tenemos identificado, a una URL por defecto.


## Determinar el rol

Tal y como vimos en el [ejemplo de seguridad con `UserDetailsService`](../SeguridadEnUDS/), la interfaz `UserDetails` define un método que nos devuelve una colección de `GrantedAuthority`, y que habitualmente, se definen como uno o varios roles para el usuario (con el prefijo `ROLE_`, por ejemplo `ROLE_ADMIN`).

La interfaz `AuthenticationSuccessHandler` nos proporciona un objeto de tipo `Authentication` con el usuario recién autenticado. En este caso, este objeto está muy relacionado con `UserDetails`. De hecho, podríamos decir que nuestro objeto de tipo `Authentication` aloja dentro una instancia de una clase que implementa la interfaz `UserDetails`, así como la colección de `GrantedAuthority` (roles) de dicho usuario. Así que obtener el rol o roles no es más que acceder al método _getter_ correspondiente.

```java
@Component
@Log
public class RoleBasedSuccessHandler 
	implements AuthenticationSuccessHandler {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

				// Lógica de redirección	
				authentication.getAuthorities();
			}


	}
```

## Obtener el rol de más peso

Si el usuario solamente tiene un rol, la colección del paso anterior solamente tendrá un elemento, pero Spring Security permite que un usuario tenga más de un rol. En este caso, queremos saber que rol es el de más peso o más importante, para redirigir al usuario a la URL más adecuada.

> También podríamos darle a elegir al usuario entre sus diferentes roles, para que fuera él el que escogiera.

**En este ejemplo, el peso de los roles se ha establecido estáticamente en un `Map`, aunque se podría buscar una implementación más adecuada.**


```java
@Component
@Log
public class RoleBasedSuccessHandler 
	implements AuthenticationSuccessHandler {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

				// Lógica de redirección	
				authentication.getAuthorities();
			}


	}

	private static Map<String, Integer> role_weight = Map.of(
			"ROLE_ADMIN", 10,
			"ROLE_USER", 1
			);
```

Para determinar el rol de más peso, podemos implementar un método que ordene los roles de un usuario en base a los pesos de dichos roles, y nos devuelva el mayor; y usar ese método para obtener dicho rol como un `String`:

```java
@Component
@Log
public class RoleBasedSuccessHandler 
	implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		// Lógica de redirección	
		// Determinar el rol de más privilegios, si el usuario tiene más de uno
		String role = getMaxRole(authentication.getAuthorities());
	}
	

	private String getMaxRole(Collection<? extends GrantedAuthority> collection) {
		List<GrantedAuthority> authoritiesList =
				new ArrayList<>(collection);
		
		// Usuario autenticado pero sin rol
		if (authoritiesList.isEmpty())
			return "ROLE_DEFAULT";
		
		return authoritiesList
			.stream()
			.map(GrantedAuthority::getAuthority)
			.sorted((role1, role2) -> 
				role_weight.getOrDefault(role2, Integer.MIN_VALUE) 
					- role_weight.getOrDefault(role1, Integer.MIN_VALUE))
			.findFirst()
			.get();
		
		
	}

	private static Map<String, Integer> role_weight = Map.of(
			"ROLE_ADMIN", 10,
			"ROLE_USER", 1
			);
```

## Redirección según el rol de más peso.

Una vez que hemos obtenido el rol de más peso, podemos redirigir al usuario a la URL adecuada según el rol. Para ello:

- Tenemos que declarar las URLs.
- Creamos un objeto que nos ayuda con la redirección.
- Creamos un método que nos ayude a _traducir_ de rol a URL.

```java
@Component
@Log
public class RoleBasedSuccessHandler 
	implements AuthenticationSuccessHandler {
	
	private RedirectStrategy redirectStrategy =
			new DefaultRedirectStrategy();
	
	private final String ROLE_USER_URL = "/web/index";
	private final String ROLE_ADMIN_URL = "/admin/index";
	private final String ROLE_DEFAULT_URL = "/login?error=Error en el rol asignado";
	
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// Determinar el rol de más privilegios, si el usuario tiene más de uno
		String role = getMaxRole(authentication.getAuthorities());

		// En función del rol de más privilegios, redirigir a la URL correcta
		String redirectUrl = determineTargetUrl(role);
		
		if (response.isCommitted()) {
			log.info("Can't redirect");
			return;
		}
		
		redirectStrategy.sendRedirect(request, response, redirectUrl);
		
		
	}
	
	private String getMaxRole(Collection<? extends GrantedAuthority> collection) {
		List<GrantedAuthority> authoritiesList =
				new ArrayList<>(collection);
		
		// Usuario autenticado pero sin rol
		if (authoritiesList.isEmpty())
			return "ROLE_DEFAULT";
		
		return authoritiesList
			.stream()
			.map(GrantedAuthority::getAuthority)
			.sorted((role1, role2) -> 
				role_weight.getOrDefault(role2, Integer.MIN_VALUE) 
					- role_weight.getOrDefault(role1, Integer.MIN_VALUE))
			.findFirst()
			.get();
		
		
	}
	
	private String determineTargetUrl(String role) {
		return switch(role) {
			case "ROLE_ADMIN" -> ROLE_ADMIN_URL;
			case "ROLE_USER" -> ROLE_USER_URL;
			default -> ROLE_DEFAULT_URL;
		};
	}
	
	private static Map<String, Integer> role_weight = Map.of(
			"ROLE_ADMIN", 10,
			"ROLE_USER", 1
			);

}
```

- [`DefaultRedirectStrategy`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/DefaultRedirectStrategy.html) es un objeto de tipo `RedirectStrategy` que nos permite **redirigir** a nuestro usuario a una URL establecida a través de una cadena de caracteres.
-  Para determinar la URL, podemos usar un [switch como expresión](https://docs.oracle.com/en/java/javase/17/language/switch-expressions.html), que resulta bastante cómodo. También podríamos haber creado un `Map<String, String>`, que _mapeara_ cada rol con su URL correspondiente.
-  La comprobación de `response.isCommitted()` sirve para saber si, por alguna causa, ya se hubiera comenzado a enviar la respuesta HTTP al navegador, ya que de ser así, no podremos realizar convenientemente al redirección.


## Configuración de nuestro `AuthenticationSuccessHandler` en la seguridad.

Por último, tenemos que modificar el código original de nuestra seguridad, para:

- Añadir como `AuthenticationSuccessHandler` la clase que acabamos de crear.
- Establecer como caché de petición una instancia de `NullRequestCache`. Esta caché sirve para que Spring "recuerde" de qué URL venía la petición que le llevó al login, y así llevarlo a esa URL tras la autenticación. **En nuestro caso, no es necesario puesto que estamos forzando el "llevar" al usuario a una URL concreta según su rol**.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final AuthenticationSuccessHandler authenticationSuccessHandler;

	// Resto del código

	@Bean
	InMemoryUserDetailsManager userDetailsService() {
		UserDetails admin = User.builder()
				.username("admin")
				.password("{noop}admin")
				.roles("ADMIN", "USER").build();
		
		UserDetails user = User.builder()
				.username("user")
				.password("{noop}1234")
				.roles("USER").build();
		
		UserDetails user2 = User.builder()
				.username("user2")
				.password("{noop}5678")
				.roles("OTHER").build();
		
		
		return new InMemoryUserDetailsManager(user, admin, user2);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		
		// Establecemos como caché de petición NullRequestCache
		// porque no nos interesa a qué URL iba el usuario, ya que
		// con el mecanismo de redirección por rol estamos forzando
		// que vaya a la página inicial según su tipo de rol.
		//
		// ROLE_USER -> /web/index
		// ROLE_ADMIN -> /admin/index
		//
		RequestCache requestCache = new NullRequestCache();

		http.authorizeHttpRequests(
				(authz) -> authz
				.requestMatchers("/css/**", "/js/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.requestCache(cache -> cache.requestCache(requestCache))
			.formLogin((loginz) -> loginz
					.loginPage("/login")
					.successHandler(authenticationSuccessHandler)
					.permitAll());

		return http.build();

	}

}

```