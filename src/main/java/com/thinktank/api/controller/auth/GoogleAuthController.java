package com.thinktank.api.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.service.auth.OAuthService;
import com.thinktank.api.service.auth.GoogleAuthService;
import com.thinktank.api.dto.auth.GoogleUserInfoResDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GoogleAuthController {

	private final GoogleAuthService googleAuthService;
	private final OAuthService oAuthService;

	@GetMapping("/google")
	public void getGoogleAuthUrl(HttpServletResponse response) throws Exception {
		response.sendRedirect(googleAuthService.getOAuthRedirectURL());
	}

	@GetMapping("/google/login")
	public ResponseEntity<GoogleUserInfoResDto> googleLogin(@RequestParam("code") String code) throws Exception {
		return ResponseEntity.ok(oAuthService.googleLogin(code));
	}
}
