
package com.spring.blog_jwt.useservices;

import org.springframework.security.core.userdetails.UserDetails;

import com.spring.blog_jwt.entities.Users;

public interface JwtService {
	// eske teen kam user nikalana , token generate karna , token valid karna
	String extractUsername(String token);

	boolean isTokenValid(String token, UserDetails userDetails);

	String generateAccessToken(Users workers);

	public String generateRefreshToken(Users workers);

	boolean isValidRefreshToken(String token, Users user);
}