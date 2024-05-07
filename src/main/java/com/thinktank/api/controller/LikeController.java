package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.like.request.LikeCreateDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.LikeService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/like")
	public ResponseEntity<String> handleLike(@RequestBody @Validated LikeCreateDto likeCreateDto,
		@Auth AuthUser authUser) {
		likeService.handleLike(likeCreateDto, authUser);
		return ResponseEntity.ok("OK");
	}
}