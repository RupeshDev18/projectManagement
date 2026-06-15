package com.example.projectManagement.security;

import com.example.projectManagement.entity.User;
import com.example.projectManagement.repositories.UserRepositories;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositories userRepositories;
    public CustomUserDetailsService(UserRepositories userRepositories){
        this.userRepositories=userRepositories;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email){

        User user= userRepositories.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Email doesn't exist"));
        return new CustomUserDetails(user);
    }
}
