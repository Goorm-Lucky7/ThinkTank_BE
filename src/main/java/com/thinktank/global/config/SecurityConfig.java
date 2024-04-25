package com.thinktank.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thinktank.global.auth.filter.JwtLoginFilter;
import com.thinktank.global.auth.filter.JwtAuthenticationFilter;
import com.thinktank.api.service.auth.JwtProviderService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;

	private final JwtProviderService jwtProviderService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {

		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(AbstractHttpConfigurer::disable);

		httpSecurity.formLogin(AbstractHttpConfigurer::disable);

		httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

		httpSecurity.authorizeHttpRequests((auth) -> auth
			.requestMatchers("/login", "/api/signup").permitAll()
			.requestMatchers("/api/reissue").permitAll()
			.anyRequest().authenticated()
		);

		httpSecurity.addFilterBefore(
			new JwtAuthenticationFilter(jwtProviderService),
			JwtLoginFilter.class
		);

		httpSecurity.addFilterAt(
			new JwtLoginFilter(authenticationManager(authenticationConfiguration), jwtProviderService),
			UsernamePasswordAuthenticationFilter.class
		);

		httpSecurity.sessionManagement((session) -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		return httpSecurity.build();
	}
}
