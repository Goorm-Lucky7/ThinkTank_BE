package com.thinktank.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.request.UserDeleteDto;
import com.thinktank.api.dto.user.response.LoginResDto;
import com.thinktank.api.dto.user.response.UserResDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.UserService;
import com.thinktank.api.service.auth.AuthenticationService;
import com.thinktank.global.auth.annotation.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;
	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> signUp(@RequestBody @Validated SignUpDto signUpDto) {
		userService.signUp(signUpDto);
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<LoginResDto> login(
		@RequestBody @Validated LoginReqDto loginReqDto, HttpServletResponse response
	) {
		return ResponseEntity.ok(authenticationService.login(loginReqDto, response));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		authenticationService.logout(request, response);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/users")
	public ResponseEntity<UserResDto> findUserDetails(@Auth AuthUser authUser) {
		return ResponseEntity.ok(userService.findUserDetails(authUser));
	}

	@DeleteMapping
	public ResponseEntity<String> removeUser(@Auth AuthUser authUser, UserDeleteDto userDeleteDto) {
		userService.removeUser(authUser, userDeleteDto);
		return ResponseEntity.ok("OK");
	}
}
