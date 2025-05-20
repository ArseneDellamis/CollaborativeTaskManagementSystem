package com.collaborativeTaskManagementSystem.taskManagement.controller;

import com.collaborativeTaskManagementSystem.taskManagement.EmailAlreadyExistsException;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AuthenticationResponse;
import com.collaborativeTaskManagementSystem.taskManagement.payload.RegisterRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.ResponseHandler;
import com.collaborativeTaskManagementSystem.taskManagement.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthenticationController {


    private final UserAuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
//            return ResponseEntity.ok(Service.register(request));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseHandler
                            .<String>builder()
                            .status(HttpStatus.CREATED)
                            .data(String.valueOf(service.register(request)))
                            .build()
                    );
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseHandler
                            .<String>builder()
                            .status(HttpStatus.CONFLICT)
                            .data(e.getMessage())
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler
                            .<String>builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authResponse = service.authenticate(request);
            return ResponseEntity.ok(
                    ResponseHandler.<AuthenticationResponse>builder()
                            .status(HttpStatus.OK)
                            .data(authResponse)
                            .build()
            );

        } catch (BadCredentialsException ex) {
            // thrown by AuthenticationManager if password is wrong
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseHandler
                            .<String>builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .data("Invalid email or password")
                            .build());
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseHandler.<String>builder()
                            .status(HttpStatus.NOT_FOUND)
                            .data(ex.getMessage())
                            .build());
        }
    }

}
