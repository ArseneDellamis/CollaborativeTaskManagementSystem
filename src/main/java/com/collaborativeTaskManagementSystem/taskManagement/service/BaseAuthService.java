package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.EmailAlreadyExistsException;
import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtService;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationResponse;
import com.collaborativeTaskManagementSystem.taskManagement.payload.RegisterRequest;
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
public abstract class BaseAuthService {
    //this service contains common methods
    protected final JwtService jwtService;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationManager manager;
    protected final UserRepository userRepository;


    //check if email already exists
    protected void validateEmail(String email) {
        if (userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(email, email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    //create user
    protected User createUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return userRepository.save(user);
    }
    //update user
    protected User UpdateUser(String email, RegisterRequest request) {

        validateEmail(email);
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        return null;
    }

    //create authentication response
    protected AuthenticationResponse createAuthenticationResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    //authenticate user
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(request.getEmail(), request.getEmail()).orElseThrow(
                ()-> new UsernameNotFoundException("User not found")
        );
        return createAuthenticationResponse(user);
    }

    //get username
    public String getUsername(String token) {
        String email= jwtService.extractUsername(token);
        User user = userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(email, email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return user.getUsername();
    }

    public User getUserInfo(String token) {

        String email= jwtService.extractUsername(token);
        return userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(email, email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
    }

    //delete user
    protected void deleteUser(String email) {
        User user = userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(email, email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        userRepository.delete(user);
    }

}
