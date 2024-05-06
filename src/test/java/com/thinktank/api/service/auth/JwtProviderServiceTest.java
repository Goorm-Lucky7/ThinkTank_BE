package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;
import static com.thinktank.global.common.util.GlobalConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.redis.TokenRepository;
import com.thinktank.global.config.TokenConfig;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;
import com.thinktank.support.fixture.JwtFixture;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@SpringBootTest(classes = {JwtProviderServiceTest.class})
@ActiveProfiles("local")
@TestPropertySource(properties = {
	"jwt.secret.access-key=test_test_test_test_test_test_test_test_test_test_test",
	"jwt.access-expire=30000",
	"jwt.refresh-expire=60480000"
})
class JwtProviderServiceTest {

	@Autowired
	JwtProviderService jwtProviderService;

	@MockBean
	TokenRepository tokenRepository;

	@MockBean
	TokenConfig tokenConfig;

	@BeforeEach
	void setUp() {
		String keyString = "test_test_test_test_test_test_test_test_test_test_test";
		byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

		when(tokenConfig.getSecretKey()).thenReturn(secretKey);
		when(tokenConfig.getAccessTokenExpire()).thenReturn(60000L);
		when(tokenConfig.getRefreshTokenExpire()).thenReturn(86400000L);
	}

	@DisplayName("generateAccessToken(): 액세스 토큰 발급 성공 - accessToken")
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

	@DisplayName("generateRefreshToken(): 리프레쉬 토큰 발급 성공 - refreshToken")
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

	@DisplayName("reGenerateToken(): 리프레쉬 토큰을 이용해서 액세스 토큰 재발급 성공 - accessToken")
	@Test
	void reGenerateToken_accessToken_success() {

	}

	@DisplayName("reGenerateToken(): 리프레쉬 토큰 정보에 해당하는 사용자가 없음 - NotFountException")
	@Test
	void reGenerateToken_user_NotFountException_fail() {

	}

	@DisplayName("reGenerateToken(): 해당 리프레쉬 토큰은 이미 재발급에 사용된 토큰 - NotFountException")
	@Test
	void reGenerateToken_JWT_NotFountException_fail() {

	}

	@DisplayName("extractToken(): 토큰 추출 성공 - Token")
	@Test
	void extractToken_token_success() {
		// GIVEN
		String accessToken = "accessToken";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(ACCESS_TOKEN_HEADER, BEARER + BLANK + accessToken);

		// WHEN
		String actual = jwtProviderService.extractAccessToken(ACCESS_TOKEN_HEADER, request);

		// THEN
		assertThat(actual).isEqualTo(accessToken);
	}

	@DisplayName("extractToken(): BEARER 타입이 아닌 토큰 추출 - NULL")
	@Test
	void extractToken_null_fail() {
		// GIVEN
		String accessToken = "accessToken";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(ACCESS_TOKEN_HEADER, accessToken);

		// WHEN
		String actual = jwtProviderService.extractAccessToken(ACCESS_TOKEN_HEADER, request);

		// THEN
		assertThat(actual).isNull();
	}

	@DisplayName("extractAuthUserByAccessToken(): 액세스 토큰 정보를 추출해 AuthUser 생성 - AuthUser")
	@Test
	void extractAuthUserByAccessToken_AuthUser_success() {
		// GIVEN
		String email = "solmoon@gmail.com";
		String nickname = "ssol";
		String accessToken = jwtProviderService.generateAccessToken(email, nickname);

		// WHEN
		AuthUser actual = jwtProviderService.extractAuthUserByAccessToken(accessToken);

		// THEN
		assertThat(actual.email()).isEqualTo(email);
		assertThat(actual.nickname()).isEqualTo(nickname);
	}

	@DisplayName("isUsable(): 토큰 유효 - TRUE")
	@Test
	void isUsable_true_success() {
		// GIVEN
		String email = "solmoon@gmail.com";
		String nickname = "ssol";
		String accessToken = jwtProviderService.generateAccessToken(email, nickname);

		MockHttpServletResponse response = new MockHttpServletResponse();

		// WHEN
		boolean actual = jwtProviderService.isUsable(accessToken, response);

		// THEN
		assertThat(actual).isTrue();
	}

	@DisplayName("isUsable(): 토큰 만료 - FALSE")
	@Test
	void isUsable_false_fail() {
		// GIVEN
		String accessToken = JwtFixture.createExpiredToken(tokenConfig.getSecretKey());

		MockHttpServletResponse response = new MockHttpServletResponse();

		// WHEN
		boolean actual = jwtProviderService.isUsable(accessToken, response);

		// THEN
		assertThat(actual).isFalse();
	}

	@DisplayName("isUsable - 토큰이 빈값이다. - NotFoundException (Empty)")
	@Test
	void isUsable_emptied_NotFoundException_fail() {
		// WHEN & THEN
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertThatThrownBy(() -> jwtProviderService.isUsable("", response))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION.getMessage());
	}

	@DisplayName("isUsable - 잘못된 토큰이다. - NotFoundException (Invalid)")
	@Test
	void isUsable_invalid_NotFoundException_fail() {
		// WHEN & THEN
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertThatThrownBy(() -> jwtProviderService.isUsable("invalid token", response))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.FAIL_INVALID_TOKEN_EXCEPTION.getMessage());
	}
}