package com.example.projectManagement.services;

import com.example.projectManagement.dto.request.LoginRequest;
import com.example.projectManagement.dto.request.RegisterRequest;
import com.example.projectManagement.dto.response.LoginResponse;
import com.example.projectManagement.dto.response.RegisterResponse;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.enums.UserRole;
import com.example.projectManagement.exceptions.EmailAlreadyExistException;
import com.example.projectManagement.exceptions.InvalidCredentialException;
import com.example.projectManagement.repositories.UserRepositories;
import com.example.projectManagement.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuthService {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepositories userRepositories, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepositories = userRepositories;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }


    public RegisterResponse register(RegisterRequest request){
        log.info("Registering user {}", request.email());
        userRepositories.findByEmail(request.email()).ifPresent((user)-> {
            throw new EmailAlreadyExistException("email already exist");
        });
        User user1=new User();
        user1.setEmail(request.email());
        user1.setName(request.name());
        user1.setPassword(passwordEncoder.encode(request.password()));
        user1.setRole(UserRole.USER);

        userRepositories.save(user1);
        return new RegisterResponse("User Register successfully");
    }

    public LoginResponse login(LoginRequest request){
        log.info("login user {} ",request.email());
        User user=userRepositories.findByEmail(request.email()).orElseThrow(()-> new InvalidCredentialException("email or Password are incorrect"));
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidCredentialException("email or Password are incorrect");
        }
        String token=jwtProvider.generateToken(request.email());
        return new LoginResponse("login success",token);
    }
}
