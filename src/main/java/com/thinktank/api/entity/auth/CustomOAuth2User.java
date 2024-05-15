package com.thinktank.api.entity.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

	private final OAuth2User oauth2User;
	private final String oauthProvider;
	private final String token;

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
		attributes.put("token", token);
		attributes.put("oauthProvider", oauthProvider);
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oauth2User.getName();
	}
}