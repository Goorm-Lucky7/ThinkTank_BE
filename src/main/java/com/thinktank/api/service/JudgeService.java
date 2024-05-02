package com.thinktank.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.judge.request.JudgeDto;
import com.thinktank.api.dto.testcase.custom.CustomTestCase;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.TestCase;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.TestCaseRepository;
import com.thinktank.global.common.util.JavaJudge;
import com.thinktank.global.common.util.JavaScriptJudge;
import com.thinktank.global.common.util.JudgeUtil;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JudgeService {
	private final PostRepository postRepository;
	private final TestCaseRepository testCaseRepository;

	public void judge(JudgeDto dto) {
		final Post post = postRepository.findById(dto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));
		final List<TestCase> testCases = testCaseRepository.findByPost(post);
		final List<CustomTestCase> customTestCases = testCases.stream()
			.map(testCase -> new CustomTestCase(testCase.getExample(), testCase.getResult()))
			.toList();

		judge(customTestCases, dto.code(), dto.language());
	}

	private void judge(List<CustomTestCase> testCases, String code, String language) {
		final JudgeUtil judgeService;

		if (language.equals("java")) {
			judgeService = new JavaJudge();
		} else if (language.equals("javascript")) {
			judgeService = new JavaScriptJudge();
		} else {
			throw new BadRequestException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION);
		}

		judgeService.executeCode(testCases, code);
	}
}
