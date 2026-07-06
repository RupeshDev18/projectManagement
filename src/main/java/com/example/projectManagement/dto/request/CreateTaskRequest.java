package com.example.projectManagement.dto.request;

import com.example.projectManagement.entity.Project;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.enums.TaskPriority;
import com.example.projectManagement.enums.TaskStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public record CreateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        Long assigneeId,
        LocalDateTime dueDate
) {
}
