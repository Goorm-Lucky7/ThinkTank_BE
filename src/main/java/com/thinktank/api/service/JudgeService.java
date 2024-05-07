package com.thinktank.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.judge.request.JudgeDto;
import com.thinktank.api.dto.testcase.custom.CustomTestCase;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserCode;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.TestCaseRepository;
import com.thinktank.api.repository.UserCodeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.common.util.JavaJudge;
import com.thinktank.global.common.util.JavaScriptJudge;
import com.thinktank.global.common.util.JudgeUtil;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JudgeService {
	private final PostRepository postRepository;
	private final TestCaseRepository testCaseRepository;
	private final UserCodeRepository userCodeRepository;
	private final UserRepository userRepository;

	public void judge(JudgeDto dto, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));
		final Post post = postRepository.findById(dto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));
		final List<CustomTestCase> testCases = testCaseRepository.findByPost(post)
			.stream()
			.map(testCase -> new CustomTestCase(testCase.getExample(), testCase.getResult()))
			.toList();

		validateJudge(testCases, dto.code(), dto.language());
		save(dto, user, post);
	}

	private void save(JudgeDto dto, User user, Post post) {
		final boolean isSolved = userCodeRepository.existsByUserAndPost(user, post);

		if (!isSolved) {
			final UserCode userCode = UserCode.create(dto.code(), post, user);
			userCodeRepository.save(userCode);
		}
	}

	private void validateJudge(List<CustomTestCase> testCases, String code, String language) {
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
    //test
}
