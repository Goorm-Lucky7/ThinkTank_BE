package com.thinktank.api.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.config.TokenConfig;
import com.thinktank.support.fixture.UserFixture;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@TestPropertySource(properties = {
	"jwt.secret.access-key=test_test_test_test_test_test_test_test_test_test_test",
	"jwt.access-expire=60000",
	"jwt.refresh-expire=86400000"
})
class JwtProviderServiceTest {

	@Autowired
	JwtProviderService jwtProviderService;

	@Autowired
	TokenConfig tokenConfig;

	@MockBean
	UserRepository userRepository;

	@DisplayName("generateAccessToken(): 액세스 토큰 발급 완료 - accessToken")
	@Test
	void generateAccessToken_accessToken_success() {
		// GIVEN
		String email = "solmoon@gmail.com";
		String nickname = "ssol";

		// WHEN
		String accessToken = jwtProviderService.generateAccessToken(email, nickname);
		Jws<Claims> actual = Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(accessToken);

		// THEN
		assertThat(actual.getPayload().get("email", String.class)).isEqualTo(email);
		assertThat(actual.getPayload().get("nickname", String.class)).isEqualTo(nickname);
	}

	@DisplayName("generateRefreshToken(): 리프레쉬 토큰 발급 완료 - refreshToken")
	@Test
	void generateRefreshToken_refreshToken_success() {
		// GIVEN
		String email = "solmoon@gmail.com";

		// WHEN
		String refreshToken = jwtProviderService.generateRefreshToken(email);
		Jws<Claims> actual = Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(refreshToken);

		// THEN
		assertThat(actual.getPayload().get("email", String.class)).isEqualTo(email);
	}

	@DisplayName("reGenerateToken(): 액세스 토큰 재발급 - refreshToken")
	@Test
	void reGenerateToken_accessToken_success() {
		// GIVEN
		User user = UserFixture.createUser();
		String refreshToken = jwtProviderService.generateRefreshToken(user.getEmail());
		MockHttpServletResponse response = new MockHttpServletResponse();

		user.updateRefreshToken(refreshToken);

		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));

		// WHEN
		String accessToken = jwtProviderService.reGenerateToken(refreshToken, response);
		Jws<Claims> actual = Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(accessToken);

		// THEN
		assertThat(actual.getPayload().get("email", String.class)).isEqualTo(user.getEmail());
		assertThat(actual.getPayload().get("nickname", String.class)).isEqualTo(user.getNickname());
	}
}