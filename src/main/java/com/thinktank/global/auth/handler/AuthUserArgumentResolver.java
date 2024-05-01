package com.thinktank.global.auth.handler;

import static com.thinktank.global.auth.AuthorizationThreadLocal.*;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.global.auth.annotation.Auth;

import jakarta.annotation.Nullable;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Objects.nonNull(parameter.getParameterAnnotation(Auth.class))
			&& parameter.getParameterType().equals(AuthUser.class);
	}

	@Override
	public Object resolveArgument(@Nullable MethodParameter parameter, ModelAndViewContainer mavContainer,
		@Nullable NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return getAuthUser();
	}
}
