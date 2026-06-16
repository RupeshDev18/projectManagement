package com.example.projectManagement.dto.response;

import com.example.projectManagement.enums.WorkspaceRole;

public record WorkspaceResponse (
        Long id,
        String name,
        String description,
        WorkspaceRole role
){
}
