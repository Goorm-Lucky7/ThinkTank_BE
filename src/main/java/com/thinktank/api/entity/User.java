package com.thinktank.api.entity;

import com.thinktank.api.dto.auth.OAuthProviderUpdateDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.request.UserUpdateDto;
import com.thinktank.api.entity.auth.OAuthProvider;
import com.thinktank.global.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tbl_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "github")
	private String github;

	@Column(name = "blog")
	private String blog;

	@Column(name = "introduce")
	private String introduce;

	@Column(name = "oauth_provider")
	@Enumerated(EnumType.STRING)
	private OAuthProvider oauthProvider;

	@Builder
	private User(String email,
		String nickname,
		String password,
		String github,
		String blog,
		String introduce) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.github = github;
		this.blog = blog;
		this.introduce = introduce;
	}

	public static User signup(SignUpDto signupDTO, String password) {
		return User.builder()
			.email(signupDTO.email())
			.nickname(signupDTO.nickname())
			.password(password)
			.github(signupDTO.github())
			.blog(signupDTO.blog())
			.introduce(signupDTO.introduce())
			.build();
	}

	public void updateUserProfile(UserUpdateDto userUpdateDto) {
		validateNicknameForUpdate(userUpdateDto.nickname());
		this.github = userUpdateDto.github();
		this.blog = userUpdateDto.blog();
		this.introduce = userUpdateDto.introduce();
	}

	private void validateNicknameForUpdate(String nickname) {
		if (nickname != null && !nickname.trim().isEmpty()) {
			this.nickname = nickname;
		}
	}

	public void updateOAuthProvider(OAuthProviderUpdateDto oauthProviderUpdateDto) {
		this.oauthProvider = OAuthProvider.findByName(oauthProviderUpdateDto.oauthProvider());
	}
}
