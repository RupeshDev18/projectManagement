package com.example.projectManagement.repositories;

import com.example.projectManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositories extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
