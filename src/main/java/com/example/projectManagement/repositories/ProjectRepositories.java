package com.example.projectManagement.repositories;

import com.example.projectManagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepositories extends JpaRepository<Project,Long> {
    List<Project> findAllByWorkspaceId(Long WorkspaceId);

    Optional<Project> findByIdAndWorkspaceId(Long id, Long workspaceId);
}
