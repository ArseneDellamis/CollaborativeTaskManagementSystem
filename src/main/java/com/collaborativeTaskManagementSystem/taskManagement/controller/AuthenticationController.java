package com.collaborativeTaskManagementSystem.taskManagement.controller;

import com.collaborativeTaskManagementSystem.taskManagement.EmailAlreadyExistsException;
import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtService;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.*;
import com.collaborativeTaskManagementSystem.taskManagement.service.UserAuthService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthenticationController {


    private final UserAuthService service;
    private final JwtService Jwtservice;
    private String jwtToken;

    // register user
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

    // login
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

    @GetMapping("/username")
    public String getUsername(@NotNull @RequestHeader("Authorization") String token) {
       jwtToken = token.replace("Bearer ", "");
       return service.getUsername(jwtToken);

    }


}
