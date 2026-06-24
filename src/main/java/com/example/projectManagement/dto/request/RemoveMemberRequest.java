package com.example.projectManagement.dto.request;

public record RemoveMemberRequest(
        Long workspaceId,
        String email
) {}
