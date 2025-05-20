package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.EmailAlreadyExistsException;
import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtService;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationResponse;
import com.collaborativeTaskManagementSystem.taskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public abstract class BaseService {
    //this service contains common methods
    protected final JwtService jwtService;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationManager manager;
    protected final UserRepository userRepository;

    protected void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    protected User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return userRepository.save(user);
    }

    protected AuthenticationResponse createAuthenticationResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                ()-> new UsernameNotFoundException("User not found")
        );
        return createAuthenticationResponse(user);
    }

}
