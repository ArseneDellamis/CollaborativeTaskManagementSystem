package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtService;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Roles;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AdminRegisterRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationResponse;
import com.collaborativeTaskManagementSystem.taskManagement.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminAuthService extends BaseAuthService {
    public AdminAuthService(JwtService jwtService,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager manager,
                            UserRepository userRepository) {
        super(jwtService, passwordEncoder, manager, userRepository);
    }

    public AuthenticationResponse register(AdminRegisterRequest request) {
        validateEmail(request.getEmail());
        // Validate role
        String roleUpperCase = request.getRole().toUpperCase();
        if (Arrays.stream(Roles.values())
                .map(Roles::name)
                .noneMatch(roleUpperCase::equals)) {
            throw new IllegalArgumentException("Invalid role. Available roles are: USER, ADMIN, PROJECT_MANAGER");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Roles.valueOf(roleUpperCase))
                .build();
        User savedUser = createUser(user);
        return createAuthenticationResponse(savedUser);
    }
}

