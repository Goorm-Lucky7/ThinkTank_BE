package com.thinktank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.judge.request.JudgeDto;
import com.thinktank.api.service.JudgeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JudgeController {
	private final JudgeService judgeService;

	@PostMapping("/judge")
	public ResponseEntity<String> judge(@RequestBody JudgeDto dto) {
		judgeService.judge(dto);

		return ResponseEntity.ok("성공");
	}
}
