package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.dto.post.response.PagePostResponseDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.PostService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping("/post")
	public ResponseEntity<String> createPost(@RequestBody @Validated PostCreateDto postCreateDto,
		@Auth AuthUser authUser) {
		postService.createPost(postCreateDto, authUser);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/posts")
	public ResponseEntity<PagePostResponseDto> getAllPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@Auth AuthUser authUser
	) {
		PagePostResponseDto posts = postService.getAllPosts(page, size, authUser);
		return ResponseEntity.ok(posts);
	}
}
