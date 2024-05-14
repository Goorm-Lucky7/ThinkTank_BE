package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.dto.post.request.PostDeleteDto;
import com.thinktank.api.dto.post.response.PagePostResponseDto;
import com.thinktank.api.dto.post.response.PostDetailResponseDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.PostService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping
	public ResponseEntity<String> createPost(@RequestBody @Validated PostCreateDto postCreateDto,
		@Auth AuthUser authUser
	) {
		postService.createPost(postCreateDto, authUser);
		return ResponseEntity.ok("OK");
	}

	@GetMapping
	public ResponseEntity<PagePostResponseDto> getAllPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@Auth(required = false) AuthUser authUser
	) {
		return ResponseEntity.ok(postService.getAllPosts(page, size, authUser));
	}

	@GetMapping("/{post-id}")
	public ResponseEntity<PostDetailResponseDto> getPostDetails(
		@PathVariable("post-id") Long postId,
		@Auth(required = false) AuthUser authUser
	) {
		return ResponseEntity.ok(postService.getPostDetail(postId, authUser));
	}

	@DeleteMapping
	public ResponseEntity<String> deletePost(
		@RequestBody @Validated PostDeleteDto postDeleteDto,
		@Auth AuthUser authUser
	) {
		postService.deletePost(postDeleteDto, authUser);
		return ResponseEntity.ok("OK");
	}

}
