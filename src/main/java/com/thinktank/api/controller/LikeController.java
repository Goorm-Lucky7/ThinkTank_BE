package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.like.request.LikeCreateDto;
import com.thinktank.api.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/like")
	public ResponseEntity<String> handleLike(@RequestBody @Validated LikeCreateDto likeCreateDto,
		@RequestHeader("userId") Long userId) {
		likeService.handleLike(likeCreateDto, userId);
		return ResponseEntity.ok("OK");
	}

}
