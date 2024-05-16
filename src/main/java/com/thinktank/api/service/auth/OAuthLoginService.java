package com.thinktank.api.service.auth;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.auth.OAuthProviderUpdateDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.entity.ProfileImage;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.OAuthProvider;
import com.thinktank.api.repository.ProfileImageRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.ConflictException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

	private final UserRepository userRepository;
	private final JwtProviderService jwtProviderService;
	private final PasswordEncoder passwordEncoder;
	private final ProfileImageRepository profileImageRepository;

	@Transactional
	public Map<String, Object> socialLogin(OAuth2User oauth2User, String oauthProvider) {
		Map<String, String> extractedAttributes = extractAttributesByProvider(oauth2User, oauthProvider);

		String email = extractedAttributes.get("email");
		String nickname = extractedAttributes.get("nickname");
		String profileImageUrl = extractedAttributes.get("profileImageUrl");
		boolean isNewUser = false;

		if (isUserSignedUp(email)) {
			if(!isUserSignedUpWithProvider(email, oauthProvider)){
				throw new ConflictException(ErrorCode.FAIL_EMAIL_CONFLICT);
			}
		} else {
			signUp(email, nickname, profileImageUrl, oauthProvider);
			isNewUser = true;
		}

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("isNewUser", isNewUser);
		responseMap.put("token", jwtProviderService.generateSocialLoginToken(email, nickname, profileImageUrl));

		return responseMap;
	}

	private boolean isUserSignedUp(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean isUserSignedUpWithProvider(String email, String providerName) {
		return userRepository.existsByEmailAndOauthProvider(email, OAuthProvider.findByName(providerName));
	}
	private Map<String, String> extractAttributesByProvider(OAuth2User oauth2User, String oauthProvider) {
		if ("google".equals(oauthProvider)) {
			return extractGoogleAttributes(oauth2User);
		} else if ("kakao".equals(oauthProvider)) {
			return extractKakaoAttributes(oauth2User);
		} else {
			throw new NotFoundException(ErrorCode.FAIL_REGISTRATION_NOT_FOUND);
		}
	}

	private Map<String, String> extractKakaoAttributes(OAuth2User oauth2User) {
		Map<String, Object> rawAttributes = oauth2User.getAttributes();
		Map<String, String> extractedAttributes = new HashMap<>();

		Map<String, Object> kakaoAccount, profile;
		kakaoAccount = (Map<String, Object>)rawAttributes.get("kakao_account");
		extractedAttributes.put("email", String.valueOf(kakaoAccount.get("email")));

		profile = (Map<String, Object>) kakaoAccount.get("profile");
		extractedAttributes.put("nickname", String.valueOf(profile.get("nickname")));
		extractedAttributes.put("profile_image_url", String.valueOf(profile.get("profile_image_url")));

		String rawPassword = generateRandomPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);

		extractedAttributes.put("password", encodedPassword);

		return extractedAttributes;
	}

	private Map<String, String> extractGoogleAttributes(OAuth2User oauth2User) {
		Map<String, Object> rawAttributes = oauth2User.getAttributes();
		Map<String, String> extractedAttributes = new HashMap<>();

		System.out.println("\n\nattr:"+rawAttributes+"\n\n");

		extractedAttributes.put("email", String.valueOf(rawAttributes.get("email")));
		extractedAttributes.put("nickname", String.valueOf(rawAttributes.get("name")));
		extractedAttributes.put("profile_image_url", String.valueOf(rawAttributes.get("picture")));

		String rawPassword = generateRandomPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);

		extractedAttributes.put("password", encodedPassword);

		return extractedAttributes;
	}

	private void signUp(String email, String nickname, String profileImageUrl, String oauthProvider) {
		String password = passwordEncoder.encode(generateRandomPassword());
		User user = User.signup(new SignUpDto(email, nickname, password, password, null, null, null), password);
		userRepository.save(user);

		user.updateOAuthProvider(new OAuthProviderUpdateDto(oauthProvider));

		ProfileImage profileImage = new ProfileImage(profileImageUrl, user);
		profileImageRepository.save(profileImage);
	}

	private String generateRandomPassword() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}
}
