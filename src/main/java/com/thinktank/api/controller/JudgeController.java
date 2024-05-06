package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.judge.request.JudgeDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.JudgeService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JudgeController {
	private final JudgeService judgeService;

	@PostMapping("/posts/submit")
	public ResponseEntity<String> judge(
		@RequestBody @Validated JudgeDto dto,
		@Auth AuthUser authUser
	) {
		judgeService.judge(dto, authUser);
		return ResponseEntity.ok("OK");
	}
}
