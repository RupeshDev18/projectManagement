package com.example.projectManagement.repositories;

import com.example.projectManagement.entity.Task;
import com.example.projectManagement.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepositories extends JpaRepository<Task,Long> {
    List<Task> findAllByProjectIdAndStatus(Long projectId, TaskStatus status);
}
