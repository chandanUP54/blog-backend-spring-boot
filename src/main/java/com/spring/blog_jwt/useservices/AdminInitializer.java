package com.spring.blog_jwt.useservices;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spring.blog_jwt.entities.Role;
import com.spring.blog_jwt.entities.RoleName;
import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.repository.RoleRepository;
import com.spring.blog_jwt.repository.UserRepository;


@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
    	
	    Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN);
		if (userRole == null) {
			userRole = new Role();
			userRole.setName(RoleName.ROLE_ADMIN);
			roleRepository.save(userRole); // Save the role first
		}
		
        // Check if the admin user exists
        if (userRepository.findByEmail("chandanmau2018@gmail.com").isEmpty()) {
            Users adminUser = new Users();
            adminUser.setEmail("chandanmau2018@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("12345"));
            adminUser.setRoles(Set.of(userRole));
            
            
            userRepository.save(adminUser);
            System.out.println("Admin account created with email: chandanmau2018@gmail.com and default password.");
        }
    }
}