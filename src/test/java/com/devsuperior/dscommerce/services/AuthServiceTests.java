package com.devsuperior.dscommerce.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

	@InjectMocks
	private AuthService authService;
	
	@Mock
	private UserService userService;
	
	private User admin, selfClient, otherClient;
	
	@BeforeEach
	void setUp() throws Exception {
		
		admin = UserFactory.createAdminUser();
		selfClient = UserFactory.createCustomClientUser(2L, "Bob");
		otherClient = UserFactory.createCustomClientUser(3L, "Maria");
	}
	
	@Test
	public void validateSelfUserOrAdminShouldDoNothingWhenAdminLogged() {
		
		Mockito.when(userService.autheticated()).thenReturn(admin);
		
		final Long userId = admin.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfUserOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfUserOrAdminShouldDoNothingWhenSelfClientLogged() {
		
		Mockito.when(userService.autheticated()).thenReturn(selfClient);
		
		final Long userId = selfClient.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfUserOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfUserOrAdminShouldThrowForbiddenExceptionWhenOtherClientLogged() {
		
		Mockito.when(userService.autheticated()).thenReturn(selfClient);
		
		final Long userId = otherClient.getId();
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			authService.validateSelfUserOrAdmin(userId);
		});
	}
}
