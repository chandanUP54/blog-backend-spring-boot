package com.spring.blog_jwt.entities;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Role implements Serializable{
	
    private static final long serialVersionUID = 1L; // Add this line

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;
}


