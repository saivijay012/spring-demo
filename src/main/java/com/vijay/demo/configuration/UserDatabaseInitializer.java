package com.vijay.demo.configuration;

import com.vijay.demo.entity.User;
import com.vijay.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Predefined users
        User user1 = new User(null, "Sai", "sai012", "client1");
        User user2 = new User(null, "Vijay", "vijay012", "client2");

        // Insert users if not already present
        if (userRepository.count() == 0) { // Avoid duplicate inserts
            userRepository.save(user1);
            userRepository.save(user2);
            System.out.println("Inserted predefined users into the database.");
        } else {
            System.out.println("Users already exist in the database. Skipping initialization.");
        }
    }
}

