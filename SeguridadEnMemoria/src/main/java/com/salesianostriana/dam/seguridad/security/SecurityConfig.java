package com.salesianostriana.dam.seguridad.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

		return authBuilder.authenticationProvider(daoAuthenticationProvider()).build();

	}

	@Bean
	InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.builder()
				.username("admin").password("{noop}admin")
				.roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		/*
		 * http .authorizeRequests()
		 * .antMatchers("/css/**","/js/**","/webjars/**").permitAll()
		 * .anyRequest().authenticated() .and() .formLogin() .loginPage("/login")
		 * .permitAll();
		 * 
		 * return http.build();
		 */

		http.authorizeHttpRequests(
				(authz) -> authz.requestMatchers("/css/**", "/js/**")
				.permitAll().anyRequest().authenticated())
			.formLogin((loginz) -> loginz
					.loginPage("/login").permitAll());

		return http.build();

	}

}
