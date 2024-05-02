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
import com.thinktank.api.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<String> createComment(@RequestBody @Validated CommentCreateDto commentCreateDto,
												HttpServletRequest request) {
		commentService.createComment(commentCreateDto, request);

		return ResponseEntity.ok("Comment Created Successfully");
	}

	@GetMapping("/posts/{post-id}/comments")
	public ResponseEntity<CommentsResponseDto> getCommentsByPostId(@PathVariable("post-id") Long postId,
												@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
		CommentsResponseDto comments = commentService.getCommentsByPostId(postId, pageIndex, pageSize);
		return ResponseEntity.ok(comments);
	}

	@DeleteMapping("/comments")
	public ResponseEntity<String> deleteComment(@RequestBody @Validated CommentDeleteDto commentDeleteDto,
		 										HttpServletRequest request) {
		commentService.deleteComment(commentDeleteDto, request);
		return ResponseEntity.ok("Comment deleted successfully.");
	}
}
