package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.UserDetailsProjectionFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	@Mock
	private CustomUserUtil userUtil;
	
	private List<UserDetailsProjection> userDetailsProjectionAdminList;
	private String existingUsername, nonExistingUsername;
	private User user;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingUsername = "maria@gmail.com";
		nonExistingUsername = "me@example.com";
		
		userDetailsProjectionAdminList = UserDetailsProjectionFactory.createCustomAdminUserDetailsProjection(existingUsername);
		user = UserFactory.createCustomClientUser(1L, existingUsername);		
		
		Mockito.when(repository.searchUsernameWithRolesByEmail(existingUsername)).thenReturn(userDetailsProjectionAdminList);
		Mockito.when(repository.searchUsernameWithRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());
		
		Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
	}
	
	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUsernameExists() {
		
		final UserDetails result = service.loadUserByUsername(existingUsername);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}
	
	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist() {
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final UserDetails result = service.loadUserByUsername(nonExistingUsername);
		});
	}
	
	@Test
	public void autheticatedShouldReturnUserWhenUsernameExists() {
		
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		
		final User result = service.autheticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}
	
	@Test
	public void autheticatedShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist() {
		
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final User result = service.autheticated();
		});
	}
	
	@Test
	public void getLoggedUserShouldReturnUserDTOWhenUserAuthenticated() {
		
		final UserService userServiceSpy = Mockito.spy(service);
		Mockito.doReturn(user).when(userServiceSpy).autheticated();
		
		final UserDTO result = userServiceSpy.getLoggedUser();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getEmail(), existingUsername);
	}
	
	@Test
	public void getLoggedUserShouldThrowUsernameNotFoundExceptionWhenUserNotAuthenticated() {
		
		final UserService userServiceSpy = Mockito.spy(service);
		Mockito.doThrow(UsernameNotFoundException.class).when(userServiceSpy).autheticated();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final UserDTO result = userServiceSpy.getLoggedUser();
		});
	}
}
