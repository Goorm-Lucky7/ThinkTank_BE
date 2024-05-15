package com.thinktank.api.entity.auth;

public enum OAuthProvider {
	KAKAO, GOOGLE;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}