package com.spring.blog_jwt.services;

import org.springframework.http.ResponseEntity;

import com.spring.blog_jwt.dto.JwtAuthenticationResponse;
import com.spring.blog_jwt.dto.SignInRequest;
import com.spring.blog_jwt.dto.SignUpRequest;
import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.exception.UserException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

	public Users signup(SignUpRequest signUpRequest) throws UserException;
	public JwtAuthenticationResponse signin(SignInRequest signInRequest);

	public Users findUserUsingJwt(String jwt) throws UserException;
	public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response);
	
}
