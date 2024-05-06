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

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.dto.comment.request.CommentDeleteDto;
import com.thinktank.api.dto.comment.response.CommentsResponseDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.CommentService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<String> createComment(@RequestBody @Validated CommentCreateDto commentCreateDto,
												@Auth AuthUser authUser) {
		commentService.createComment(commentCreateDto, authUser);

		return ResponseEntity.ok("Comment Created Successfully");
	}

	@GetMapping("/posts/{post-id}/comments")
	public ResponseEntity<CommentsResponseDto> getCommentsByPostId(
												@PathVariable("post-id") Long postId, @Auth AuthUser authUser,
												@RequestParam(defaultValue = "0") int pageIndex,
												@RequestParam(defaultValue = "10") int pageSize) {
		CommentsResponseDto comments = commentService.getCommentsByPostId(postId, authUser, pageIndex, pageSize);

		return ResponseEntity.ok(comments);
	}

	@DeleteMapping("/comments")
	public ResponseEntity<String> deleteComment(@RequestBody @Validated CommentDeleteDto commentDeleteDto,
												@Auth AuthUser authUser) {
		commentService.deleteComment(commentDeleteDto, authUser);
		return ResponseEntity.ok("Comment deleted successfully.");
	}
}
