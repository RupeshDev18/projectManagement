package com.example.projectManagement.services;

import com.example.projectManagement.dto.request.RegisterRequest;
import com.example.projectManagement.dto.response.RegisterResponse;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.enums.UserRole;
import com.example.projectManagement.repositories.UserRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private final UserRepositories userRepositories;

    public AuthService(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }

//    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request){
        log.info("Registering user {}", request.email());
        Optional<User> user=userRepositories.findByEmail(request.email());
        user.ifPresent(user-> {
            throw new EmailAlreadyExist ("email already exist");
        });
        User user1=new User();
        user1.setEmail(request.email());
        user1.setName(request.name());
        user1.setPassword((new BCryptPasswordEncoder()).encode(request.password())); // To Be Replaced by PasswordEncoder of SecurityConfig
        user1.setRole(UserRole.ADMIN);

        userRepositories.save(user1);
        return new RegisterResponse("User Register successfully");

    }
}
