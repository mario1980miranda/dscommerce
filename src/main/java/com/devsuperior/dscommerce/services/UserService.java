package com.devsuperior.dscommerce.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private UserRepository repository;
	
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final List<UserDetailsProjection> result = this.repository.searchUsernameWithRolesByEmail(username);
		
		if (result.size() == 0)
			throw new UsernameNotFoundException("User not found");
		
		User user = new User();
		user.setEmail(username);
		user.setPassword(result.get(0).getPassword());
		
		for (UserDetailsProjection projection : result)
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
			
		return user;
	}
	
	@Transactional(readOnly = true)
	public UserDTO getLoggedUser() {
		final User user = this.autheticated();
		return new UserDTO(user);
	}
	
	protected User autheticated() {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			final Jwt jwtPrincipal = (Jwt)auth.getPrincipal();
			final String username = jwtPrincipal.getClaim("username");
			return this.repository.findByEmail(username).get();
		} catch (Exception e) {
			throw new UsernameNotFoundException("User not found");
		}
	}

}
