package com.example.projectManagement.entity;


import com.example.projectManagement.enums.UserRole;
import com.example.projectManagement.enums.WorkspaceRole;
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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_user",
                        columnNames = {"workspace_id", "user_id"}
                )
        }
)
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "workspace_id",nullable = false)
    @ManyToOne
    private Workspace workspace;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    private WorkspaceRole role;
}
