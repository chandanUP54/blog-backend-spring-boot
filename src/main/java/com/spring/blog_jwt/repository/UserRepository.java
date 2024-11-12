package com.spring.blog_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.blog_jwt.entities.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query(value = "select * from ourusers where email = ?1", nativeQuery = true)
    Optional<Users> findByEmail(String email);
}
