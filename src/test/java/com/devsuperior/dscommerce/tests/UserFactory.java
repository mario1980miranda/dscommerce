package com.devsuperior.dscommerce.tests;

import java.time.LocalDate;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

public class UserFactory {

	public static User createClientUser() {
		User user = new User(1L, "Maria", "maria@gmail.com", "(123)456-7890", LocalDate.parse("2001-07-25"), "$2a$10$ToRBlIrQyRN5yq7LZ29gpuyCc8HsDr3Z5tQQ.5IVOHUOsfiCntrie");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}
	
	public static User createAdminUser() {
		User user = new User(2L, "Alex", "alex@gmail.com", "(123)456-7890", LocalDate.parse("1987-12-13"), "$2a$10$ToRBlIrQyRN5yq7LZ29gpuyCc8HsDr3Z5tQQ.5IVOHUOsfiCntrie");
		user.addRole(new Role(2L, "ROLE_ADMIN"));
		return user;
	}
	
	public static User createCustomClientUser(Long id, String username) {
		User user = new User(id, "Maria", username, "(123)456-7890", LocalDate.parse("2001-07-25"), "$2a$10$ToRBlIrQyRN5yq7LZ29gpuyCc8HsDr3Z5tQQ.5IVOHUOsfiCntrie");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}
	
	public static User createCustomAdminUser(Long id, String username) {
		User user = new User(id, "Alex", username, "(123)456-7890", LocalDate.parse("1987-12-13"), "$2a$10$ToRBlIrQyRN5yq7LZ29gpuyCc8HsDr3Z5tQQ.5IVOHUOsfiCntrie");
		user.addRole(new Role(2L, "ROLE_ADMIN"));
		return user;
	}
}
