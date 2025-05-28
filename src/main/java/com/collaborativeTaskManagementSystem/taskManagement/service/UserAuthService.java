package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtService;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Roles;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationResponse;
import com.collaborativeTaskManagementSystem.taskManagement.payload.RegisterRequest;
import com.collaborativeTaskManagementSystem.taskManagement.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService extends BaseAuthService {
    public UserAuthService(JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager manager,
                           UserRepository userRepository) {
        super(jwtService, passwordEncoder, manager, userRepository);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        validateEmail(request.getEmail());
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Roles.USER)
                .build();
        User savedUser = createUser(user);
        return createAuthenticationResponse(savedUser);
    }



}

