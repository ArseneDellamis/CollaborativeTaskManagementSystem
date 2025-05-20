package com.collaborativeTaskManagementSystem.taskManagement.controller;

import com.collaborativeTaskManagementSystem.taskManagement.EmailAlreadyExistsException;
import com.collaborativeTaskManagementSystem.taskManagement.payload.AdminRegisterRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.RegisterRequest;
import com.collaborativeTaskManagementSystem.taskManagement.payload.ResponseHandler;
import com.collaborativeTaskManagementSystem.taskManagement.service.AdminAuthService;
import com.collaborativeTaskManagementSystem.taskManagement.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/auth")
public class AdminAuthController {
    private final AdminAuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdminRegisterRequest request) {
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
}
