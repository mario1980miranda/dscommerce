package com.devsuperior.dscommerce.services;

import org.springframework.stereotype.Service;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;

@Service
public class AuthService {

	private UserService userService;

	public AuthService(UserService userService) {
		this.userService = userService;
	}

	public void validateSelfUserOrAdmin(final Long userId) {
		User user = this.userService.autheticated();
		if (!user.hasRole("ROLE_ADMIN") && !user.getId().equals(userId)) {
			throw new ForbiddenException();
		}
	}
}
