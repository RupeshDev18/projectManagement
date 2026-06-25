package com.example.projectManagement.dto.request;

import com.example.projectManagement.entity.Workspace;
import com.example.projectManagement.enums.ProjectStatus;
import jakarta.persistence.*;

public record CreateProjectRequest(
        String name,
        String description,
        ProjectStatus status,
        Long workspaceId
) {
    
}
