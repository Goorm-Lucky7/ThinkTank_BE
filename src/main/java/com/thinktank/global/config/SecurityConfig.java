package com.thinktank.global.config;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.thinktank.api.service.auth.CustomOAuth2UserService;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.auth.filter.AuthenticationFilter;
import com.thinktank.global.auth.handler.CustomAuthenticationFailureHandler;
import com.thinktank.global.auth.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtProviderService jwtProviderService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public SecurityConfig(
		JwtProviderService jwtProviderService, CustomOAuth2UserService customOAuth2UserService,
		CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
		CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
	) {
		this.jwtProviderService = jwtProviderService;
		this.customOAuth2UserService = customOAuth2UserService;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;

		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers("/h2-console/**")
			.requestMatchers("/api/signup")
			.requestMatchers("/api/login")
			.requestMatchers("/api/reissue");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS));

		httpSecurity.authorizeHttpRequests((auth) -> auth
			.requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/users/profile").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/signup/*").permitAll()
			.anyRequest().authenticated()
		);

		httpSecurity.oauth2Login((oauth) -> oauth
			.loginPage("/login")
			.authorizationEndpoint(authorization -> authorization
				.baseUri("/oauth2/authorization")
			)
			.redirectionEndpoint(redirection -> redirection
				.baseUri("/login/oauth2/*")
			)
			.userInfoEndpoint(userInfo -> userInfo
				.userService(customOAuth2UserService)
			)
			.successHandler(customAuthenticationSuccessHandler)
			.failureHandler(customAuthenticationFailureHandler)
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
