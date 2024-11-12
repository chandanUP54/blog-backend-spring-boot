package com.spring.blog_jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.blog_jwt.entities.Token;

import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
	@Query("""
			SELECT t FROM Token t
			JOIN t.users u
			WHERE u.id = :userId AND t.loggedOut = false
			""")
	List<Token> findAllAccessTokensByUser(Integer userId);

	Optional<Token> findByAccessToken(String token);

	Optional<Token> findByRefreshToken(String token);

	@Modifying
	@Transactional // Important for modifying queries
	@Query(value = "DELETE FROM token WHERE user_id = ?1", nativeQuery = true)
	void deleteTokenForUser(int userId);

}