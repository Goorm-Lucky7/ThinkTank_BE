package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.response.LoginResDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProviderService jwtProviderService;

	@Transactional
	public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {
		final User user = findByUserEmail(loginReqDto.email());
		validatePasswordMatch(loginReqDto.password(), user.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);

		return new LoginResDto(accessToken);
	}

	private User findByUserEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
	}

	private void validatePasswordMatch(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}
}
