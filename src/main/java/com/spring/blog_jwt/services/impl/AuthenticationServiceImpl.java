package com.spring.blog_jwt.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.blog_jwt.dto.JwtAuthenticationResponse;
import com.spring.blog_jwt.dto.SignInRequest;
import com.spring.blog_jwt.dto.SignUpRequest;
import com.spring.blog_jwt.entities.Role;
import com.spring.blog_jwt.entities.RoleName;
import com.spring.blog_jwt.entities.Token;
import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.exception.UserException;
import com.spring.blog_jwt.repository.RoleRepository;
import com.spring.blog_jwt.repository.TokenRepository;
import com.spring.blog_jwt.repository.UserRepository;
import com.spring.blog_jwt.services.AuthenticationService;
import com.spring.blog_jwt.useservices.JwtService;
import com.spring.blog_jwt.useservices.UserInfoUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserInfoUserDetailsService userServiceDef;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

	@Override
	public Users signup(SignUpRequest request) throws UserException {

		System.out.println("req-> " + request);

		String username = request.getEmail();
		Optional<Users> optionalUser = userRepository.findByEmail(username);

		if (optionalUser.isPresent()) {
			throw new UserException("Username is already taken");
		}
		Users user = new Users();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
		if (userRole == null) {
			userRole = new Role();
			userRole.setName(RoleName.ROLE_USER);
			roleRepository.save(userRole); // Save the role first
		}
		user.setRoles(Set.of(userRole));// Now set the user role

		user = userRepository.save(user);

		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);

		saveUserToken(accessToken, refreshToken, user);

		return user;

	}

	@Override
	public JwtAuthenticationResponse signin(SignInRequest signInRequest) {

		System.out.println("helloxx");

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

		var workers = userRepository.findByEmail(signInRequest.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		var accessToken = jwtService.generateAccessToken(workers);
		var refreshToken = jwtService.generateRefreshToken(workers);

		revokeAllTokenByUser(workers);
		saveUserToken(accessToken, refreshToken, workers);

		JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
		jwtAuthenticationResponse.setAccessToken(accessToken);
		jwtAuthenticationResponse.setRefreshToken(refreshToken);

		jwtAuthenticationResponse.setRole(workers.getRoles().iterator().next().getName().toString());

		return jwtAuthenticationResponse;

	}

	private void revokeAllTokenByUser(Users user) {
		List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
		if (validTokens.isEmpty()) {
			return;
		}

		validTokens.forEach(t -> {
			t.setLoggedOut(true);
		});

		tokenRepository.saveAll(validTokens);
	}

	private void saveUserToken(String accessToken, String refreshToken, Users user) {
		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		token.setLoggedOut(false);
		token.setUsers(user);
		tokenRepository.save(token);
	}

	@Override
	public Users findUserUsingJwt(String jwt) throws UserException {

		String userEmail = jwtService.extractUsername(jwt);// error

		// UserDetails userDetails = userServiceDef.loadUserByUsername(userEmail);

		Users users = userRepository.findByEmail(userEmail).orElseThrow();

		if (users == null) {
			throw new UserException("user not found with email: " + userEmail);
		}

//		if (jwtService.isTokenValid(jwt, userDetails)) {
//
//			return users;
//		}

		return users;

	}

	public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		String token = authHeader.substring(7);

		// extract username from token
		String username = jwtService.extractUsername(token);

		// check if the user exist in database
		Users user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("No user found"));

		// check if the token is valid
		if (jwtService.isValidRefreshToken(token, user)) {
			// generate access token
			String accessToken = jwtService.generateAccessToken(user);
			String refreshToken = jwtService.generateRefreshToken(user);

			revokeAllTokenByUser(user);
			saveUserToken(accessToken, refreshToken, user);

			return new ResponseEntity(new JwtAuthenticationResponse(accessToken, refreshToken, user.getRoles().iterator().next().getName().toString()), HttpStatus.OK);

		}

		return new ResponseEntity(HttpStatus.UNAUTHORIZED);

	}

}
