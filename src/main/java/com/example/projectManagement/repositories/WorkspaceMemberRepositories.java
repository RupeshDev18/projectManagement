package com.example.projectManagement.repositories;

import com.example.projectManagement.entity.User;
import com.example.projectManagement.entity.Workspace;
import com.example.projectManagement.entity.WorkspaceMember;
import com.example.projectManagement.enums.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepositories extends JpaRepository<WorkspaceMember,Long> {
    Optional<WorkspaceMember> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);

    boolean existsByUserIdAndWorkspaceId(Long userId,Long workspaceId);

    List<WorkspaceMember> findAllByUserId(Long userId);

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

//    @Query("""
//       SELECT wm.workspace
//       FROM WorkspaceMember wm
//       WHERE wm.user.id = :userId
//       """)
//    List<Workspace> findWorkspacesByUserId(Long userId);

    long countByWorkspaceIdAndRole(Long workspaceId, WorkspaceRole role);

    Long user(User user);
}
