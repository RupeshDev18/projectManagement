package com.example.projectManagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberRequest(
        Long workspaceId,
        @NotBlank @Email String email
) {
}
