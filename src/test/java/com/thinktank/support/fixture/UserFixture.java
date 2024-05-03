package com.thinktank.support.fixture;

import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;

public class UserFixture {

	public static User createUser() {
		return User.signup(new SignUpDto("solmoon@gmail.com", "ssol", "123456", "123456", "", "", ""), "123456");
	}

	public static AuthUser createAuthUser() {
		return AuthUser.create("solmoon@gmail.com", "ssol");
	}

	public static SignUpDto createSignUpRequest(String email, String nickname, String password, String checkPassword) {
		return SignUpDto.builder()
			.email(email)
			.nickname(nickname)
			.password(password)
			.checkPassword(checkPassword)
			.build();
	}

	public static LoginReqDto createLoginRequest(String email, String password) {
		return new LoginReqDto(email, password);
	}

	public static LoginReqDto createLoginRequest(User user) {
		return new LoginReqDto(user.getEmail(), user.getPassword());
	}

	public static LoginReqDto createLoginRequest(SignUpDto signUpDto) {
		return new LoginReqDto(signUpDto.email(), signUpDto.password());
	}
}
