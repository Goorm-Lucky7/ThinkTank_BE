package com.thinktank.global.config;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.auth.filter.AuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtProviderService jwtProviderService;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public SecurityConfig(
		JwtProviderService jwtProviderService,
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
	) {
		this.jwtProviderService = jwtProviderService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers("/h2-console/**")
			.requestMatchers("/api/signup")
			.requestMatchers("/api/login")
			.requestMatchers("/api/posts/*/comments")
			.requestMatchers("/api/kakao")
			.requestMatchers("/api/kakao/login")
			.requestMatchers("/api/google")
			.requestMatchers("/api/google/login")
			.requestMatchers("/api/oauth/authorize");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS));

		httpSecurity.authorizeHttpRequests((auth) -> auth
			.anyRequest().permitAll()
		);

		httpSecurity.addFilterBefore(
			new AuthenticationFilter(jwtProviderService, handlerExceptionResolver),
			UsernamePasswordAuthenticationFilter.class
		);

		httpSecurity.exceptionHandling((exceptionHandling) -> {
			HttpStatusEntryPoint httpStatusEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
			exceptionHandling.authenticationEntryPoint(httpStatusEntryPoint);
		});

		return httpSecurity.build();
	}
}
