
package com.spring.blog_jwt.useservices;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.secret}")
	private String SECRET;

	@Value("${jwt.accessTokenExpire}")
	private long accessTokenExpire;

	@Value("${jwt.refreshTokenExpire}")
	private long refreshTokenExpire;

	@Autowired
	private TokenRepository tokenRepository;

	public String generateAccessToken(Users user) {
		return generateToken(user, accessTokenExpire);
	}

	public String generateRefreshToken(Users user) {
		return generateToken(user, refreshTokenExpire);
	}

	public String generateToken(Users userDetails, long expireTime) {
		Map<String, Object> claims = new HashMap<>();
//		    claims.put("role", role); 
//		    claims.put("authorities", Collections.singletonList(role));

		return Jwts.builder().setClaims(claims).setSubject(userDetails.getEmail())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expireTime)) // 1 days
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

//	public String generateToken(String role,Users userDetails) {
//       
//	   Map<String, Object> claims = new HashMap<>();
	// claims.put("role", role);
//		
//		return Jwts.builder().setClaims(claims)
//				.setSubject(userDetails.getEmail())
//				.setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis()+1000*60*24*7)) //7 days
//				.signWith(getSignKey(), SignatureAlgorithm.HS256)
//				.compact();
//	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {

		String username = extractUsername(token);
		boolean validToken = tokenRepository.findByAccessToken(token).map(t -> !t.isLoggedOut()).orElse(false);

		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && validToken;
	}

	public boolean isValidRefreshToken(String token, Users users) {
		String username = extractUsername(token);

		boolean validRefreshToken = tokenRepository.findByRefreshToken(token).map(t -> !t.isLoggedOut()).orElse(false);

		return (username.equals(users.getEmail())) && !isTokenExpired(token) && validRefreshToken;
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

}
