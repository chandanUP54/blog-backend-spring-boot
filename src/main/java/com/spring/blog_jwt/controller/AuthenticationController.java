package com.spring.blog_jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.blog_jwt.dto.JwtAuthenticationResponse;
import com.spring.blog_jwt.dto.SignInRequest;
import com.spring.blog_jwt.dto.SignUpRequest;
import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.exception.UserException;
import com.spring.blog_jwt.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
//@RequestMapping
@CrossOrigin("*")
public class AuthenticationController {

	
	// accessible to all
	@GetMapping("/hello")
	public String hello() {
		return "welcome";
	}

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<Users> signup(@RequestBody SignUpRequest signUpRequest) throws UserException {
		return ResponseEntity.ok(authenticationService.signup(signUpRequest));
	}

	@PostMapping("/login")
	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {
		
		return ResponseEntity.ok(authenticationService.signin(signInRequest));
	}

	@PostMapping("/refresh_token")
	public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
		return authenticationService.refreshToken(request, response);
	}
	
}
