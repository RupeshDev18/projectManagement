package com.example.projectManagement.entity;


import com.example.projectManagement.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class WorkspaceMember {

    @Id
    private Long id;

    @JoinColumn
    @ManyToOne
    private Workspace workspace;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;
}
