package com.example.projectManagement.controllers;

import com.example.projectManagement.dto.request.RegisterRequest;
import com.example.projectManagement.dto.response.RegisterResponse;
import com.example.projectManagement.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request){
        return this.authService.register(request);
    }
}
