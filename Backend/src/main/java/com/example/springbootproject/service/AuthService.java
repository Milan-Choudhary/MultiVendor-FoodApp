package com.example.springbootproject.service;

import com.example.springbootproject.dto.JwtResponse;
import com.example.springbootproject.dto.LoginRequest;
import com.example.springbootproject.dto.SignupRequest;
import com.example.springbootproject.model.User;
import com.example.springbootproject.repository.UserRepository;
import com.example.springbootproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // --- SIGNUP LOGIC ---
    public User registerUser(SignupRequest request) {
        // 1. Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // 2. Create new user entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 3. Hash the password for security
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Assign role (e.g., ROLE_CUSTOMER, ROLE_VENDOR)
        user.setRole(request.getRole());

        // 5. Save to database
        return userRepository.save(user);
    }

    // --- LOGIN LOGIC ---
    public JwtResponse loginUser(LoginRequest request) {
        // 1. Find the user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 2. Compare the raw password with the hashed password in the DB
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                // 3. If password matches, generate the JWT Token
                String token = jwtUtil.generateToken(user.getEmail());

                // 4. Return the token and user details to the frontend
                return new JwtResponse(token, user.getRole(), user.getEmail());
            }
        }

        // If email not found OR password doesn't match
        throw new RuntimeException("Invalid email or password");
    }
}