package com.thinktank.api.service.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.config.TokenConfig;

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
	void success_generateAccessToken() {
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

	@Test
	void generateRefreshToken() {
	}

	@Test
	void reGenerateToken() {
	}

	@Test
	void extractAccessToken() {
	}

	@Test
	void extractRefreshToken() {
	}

	@Test
	void extractAuthUserByAccessToken() {
	}

	@Test
	void isUsable() {
	}
}