package com.spring.blog_jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.exception.UserException;
import com.spring.blog_jwt.repository.UserRepository;
import com.spring.blog_jwt.services.AuthenticationService;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	private UserRepository ourUserRepo;
	
	@Autowired
	private AuthenticationService authenticationService;
	

	@GetMapping("/helloUser")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String helloUser() {
		return "Hello User....";
	}

	@GetMapping("/helloAdmin")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String helloAdmin() {
		return "Hello Admin.....";
	}
	
	//get by ADMIN AND USER BOTH
	@GetMapping("/helloAll")
	public String helloAll() {
		return "Hello All....";
	}
	
	@GetMapping("/profile")
	public Users findUserProfileUsingJwt(@RequestHeader("Authorization") String jwt) throws UserException {
		jwt=jwt.substring(7);
		Users user=authenticationService.findUserUsingJwt(jwt);
		return user;
	}

	
	@GetMapping("/users/all")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Object> getAllUSers() {
		return ResponseEntity.ok(ourUserRepo.findAll());
	}

	@GetMapping("/users/single")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
	public ResponseEntity<Object> getMyDetails() {
		return ResponseEntity.ok(ourUserRepo.findByEmail(getLoggedInUserDetails().getUsername()));
	}

	
	
	
	public UserDetails getLoggedInUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			return (UserDetails) authentication.getPrincipal();
		}
		return null;
	}
}
