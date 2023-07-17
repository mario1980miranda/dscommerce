package com.devsuperior.dscommerce.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomUserUtil {

	public String getLoggedUsername() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final Jwt jwtPrincipal = (Jwt) auth.getPrincipal();
		return jwtPrincipal.getClaim("username");
	}
}
