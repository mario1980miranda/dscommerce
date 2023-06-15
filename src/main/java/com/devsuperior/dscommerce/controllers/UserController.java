package com.devsuperior.dscommerce.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getLoggedUser() {
		final UserDTO dto = this.service.getLoggedUser();
		return ResponseEntity.ok(dto);
	}

}
