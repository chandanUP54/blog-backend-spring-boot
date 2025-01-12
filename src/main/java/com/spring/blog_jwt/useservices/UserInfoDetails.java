package com.spring.blog_jwt.useservices;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.spring.blog_jwt.entities.Users;

public class UserInfoDetails implements UserDetails {
	private String email;
	private String password;
	private List<GrantedAuthority> authorities;

	public UserInfoDetails(Users ourUser) {

		this.email = ourUser.getEmail();
		this.password = ourUser.getPassword();
		this.authorities = ourUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
