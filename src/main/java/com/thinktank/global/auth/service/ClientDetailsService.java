package com.thinktank.global.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userData = userRepository.findByEmail(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		return new ClientDetails(userData);
	}
}
