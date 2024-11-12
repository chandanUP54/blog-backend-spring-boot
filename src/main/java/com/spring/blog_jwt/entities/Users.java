package com.spring.blog_jwt.entities;

import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ourusers")
@AllArgsConstructor
@NoArgsConstructor
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(unique = true)
	private String email;
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
	private Set<Comment> comments;

	@OneToMany(mappedBy = "users", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Token> tokens;

	@Override
	public String toString() {
		return "Users{" + "id=" + id + ", email='" + email + '\'' + ", roles='" + roles + '\'' +
		// Avoid printing tokens directly to prevent recursion
				", tokensCount=" + (tokens != null ? tokens.size() : 0) + '}';
	}

}
