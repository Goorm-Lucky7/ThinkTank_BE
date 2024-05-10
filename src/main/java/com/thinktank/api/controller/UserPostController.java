package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.post.response.PagePostProfileResponseDto;
import com.thinktank.api.dto.problemType.request.ProblemTypeDto;
import com.thinktank.api.service.UserPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
public class UserPostController {
	private final UserPostService userPostService;

	@GetMapping("/profile")
	public ResponseEntity<PagePostProfileResponseDto> getProfilePosts(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestBody @Validated ProblemTypeDto problemTypeDto) {
		PagePostProfileResponseDto profilePosts = userPostService.getProfilePosts(page, size, problemTypeDto);
		return ResponseEntity.ok(profilePosts);
	}
}
