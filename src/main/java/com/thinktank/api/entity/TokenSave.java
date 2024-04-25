package com.thinktank.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenSave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Column(name = "expiration")
	private String expiration;

	@Builder
	public TokenSave(String username, String refreshToken, String expiration) {
		this.username = username;
		this.refreshToken = refreshToken;
		this.expiration = expiration;
	}
}
