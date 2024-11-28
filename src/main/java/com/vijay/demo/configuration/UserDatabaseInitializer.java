package com.vijay.demo.configuration;

import com.vijay.demo.entity.User;
import com.vijay.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Predefined users
        User user1 = new User(null, "sai", passwordEncoder.encode("sai123"), "admin");
        User user2 = new User(null, "vijay", passwordEncoder.encode("vijay123"), "user");
        User user3 = new User(null, "john", passwordEncoder.encode("john123"), "user,admin");

        // Insert users if not already present
        if (userRepository.count() == 0) { // Avoid duplicate inserts
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            System.out.println("Inserted predefined users into the database.");
        } else {
            System.out.println("Users already exist in the database. Skipping initialization.");
        }
    }
}

