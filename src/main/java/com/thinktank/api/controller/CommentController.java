package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<String> createComment(@RequestBody @Validated CommentCreateDto commentCreateDto) {
		commentService.create(commentCreateDto);

		return ResponseEntity.ok("Comment Created Successfully");
	}
}
