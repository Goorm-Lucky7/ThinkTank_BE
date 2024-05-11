package com.thinktank.api.controller.auth;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.auth.KakaoUserInfoResDto;
import com.thinktank.api.service.auth.KakaoAuthService;
import com.thinktank.api.service.auth.OAuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KakaoAuthController {

	private final KakaoAuthService kakaoAuthService;
	private final OAuthService oAuthService;

	@GetMapping("/kakao")
	public void getKakaoAuthUri(HttpServletResponse response) throws Exception {
		response.sendRedirect(kakaoAuthService.responseUri());
	}

	@GetMapping("/kakao/login")
	public ResponseEntity<KakaoUserInfoResDto> kakaoLogin(@RequestParam("code") String code) throws IOException {
		return ResponseEntity.ok(oAuthService.kakaoLogin(code));
	}
}
