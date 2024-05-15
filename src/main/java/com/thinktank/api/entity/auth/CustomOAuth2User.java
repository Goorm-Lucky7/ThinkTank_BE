package com.thinktank.api.entity.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {
	private OAuth2User oauth2User;
	private String token;
	private String oauthProvider;

	public CustomOAuth2User(OAuth2User oauth2User, String oauthProvider, String token) {
		this.oauth2User = oauth2User;
		this.oauthProvider = oauthProvider;
		this.token = token;
	}

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

	public String getToken() {
		return token;
	}
}