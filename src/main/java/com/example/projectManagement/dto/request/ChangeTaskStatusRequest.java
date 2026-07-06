package com.example.projectManagement.dto.request;

import com.example.projectManagement.enums.TaskStatus;

public record ChangeTaskStatusRequest(
        Long taskId,
        TaskStatus status
) {}
