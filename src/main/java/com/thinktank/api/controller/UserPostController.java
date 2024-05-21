package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.post.response.PagePostProfileResponseDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.UserPostService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserPostController {
	private final UserPostService userPostService;

	@GetMapping("/profile")
	public ResponseEntity<PagePostProfileResponseDto> getProfilePosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam String value,
		@RequestParam String email,
		@Auth(required = false) AuthUser authUser) {
		PagePostProfileResponseDto profilePosts = userPostService.getProfilePosts(page, size, value, email,
			authUser);
		return ResponseEntity.ok(profilePosts);
	}
}
