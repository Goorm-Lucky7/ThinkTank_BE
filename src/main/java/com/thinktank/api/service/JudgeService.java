package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.judge.request.JudgeDto;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.global.common.util.JavaJudge;
import com.thinktank.global.common.util.JudgeUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JudgeService {
	private final PostRepository postRepository;

	public void judge(JudgeDto dto) {
		final JudgeUtil judgeManager = new JavaJudge();

		judgeManager.executeCode(dto.testCases(), dto.code());
	}
}
