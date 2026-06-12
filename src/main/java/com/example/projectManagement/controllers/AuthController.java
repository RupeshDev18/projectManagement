package com.example.projectManagement.controllers;

import com.example.projectManagement.dto.request.LoginRequest;
import com.example.projectManagement.dto.request.RegisterRequest;
import com.example.projectManagement.dto.response.LoginResponse;
import com.example.projectManagement.dto.response.RegisterResponse;
import com.example.projectManagement.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.login(request));
    }
}
