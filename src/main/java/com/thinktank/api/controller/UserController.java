package com.thinktank.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.user.request.SignupDTO;
import com.thinktank.api.service.UserService;
import com.thinktank.api.service.auth.AuthorizationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	private final AuthorizationService authorizationService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody @Validated SignupDTO signupDTO) {

		userService.signup(signupDTO);

		return ResponseEntity.ok("OK");
	}

	@PostMapping("/reissue")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> authorizationTokenIssue(HttpServletRequest request, HttpServletResponse response) {

		authorizationService.refreshAccessToken(request, response);

		return ResponseEntity.ok("OK");
	}
}
