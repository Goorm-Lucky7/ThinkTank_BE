package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.dto.post.response.PagePostResponseDto;
import com.thinktank.api.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping("/post")
	public ResponseEntity<?> createPost(@RequestBody @Validated PostCreateDto postCreateDto) {
		postService.createPost(postCreateDto);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/posts")
	public ResponseEntity<PagePostResponseDto> getAllPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestHeader("userId") Long userId
	) {
		PagePostResponseDto posts = postService.getAllPosts(page, size, userId);
		return ResponseEntity.ok(posts);
	}
}
