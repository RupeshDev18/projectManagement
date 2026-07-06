package com.example.projectManagement.controllers;

import com.example.projectManagement.dto.request.CreateTaskRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService){
        this.taskService=taskService;
    }

    public ResponseEntity<StandardResponse> createTask(@Valid CreateTaskRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.taskService.createTask(request));
    }
}
