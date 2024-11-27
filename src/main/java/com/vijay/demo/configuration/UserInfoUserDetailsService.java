package com.vijay.demo.configuration;

import com.vijay.demo.entity.User;
import com.vijay.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found = " + username));

        // Return custom UserInfoUserDetails object
        return new UserInfoUserDetails(user);
    }

    // Can do this as well to ignore UserInfoUserDetails class - uncomment below code for better understanding
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Find the user by username from the database
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found = " + username));
//
//        // Fetch roles from the user entity (assuming user.getRoles() returns a collection of roles)
//        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role))  // Map each role to a SimpleGrantedAuthority
//                .collect(Collectors.toSet());
//
//        // Return a Spring Security User object with multiple roles
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                authorities);  // Multiple roles will be passed here
//    }
}

