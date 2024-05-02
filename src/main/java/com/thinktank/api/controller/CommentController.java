package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.dto.comment.response.CommentsResponse;
import com.thinktank.api.service.CommentService;
import com.thinktank.api.service.auth.JwtProviderService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<String> createComment(@RequestHeader("Authorization") String authToken,
												@RequestBody @Validated CommentCreateDto commentCreateDto) {
		commentService.createComment(commentCreateDto);

		return ResponseEntity.ok("Comment Created Successfully");
	}

	@GetMapping("/api/post/{post-id}/comments")
	public ResponseEntity<CommentsResponse> getCommentsByPostId(@RequestHeader("Authorization") String authToken,
												@PathVariable("post-id") Long postId,
												@RequestParam int pageIndex, @RequestParam int pageSize) {
		CommentsResponse comments = commentService.getCommentsByPostId(postId, pageIndex, pageSize);
		return ResponseEntity.ok(comments);
	}
}
