package com.example.projectManagement.controllers;


import com.example.projectManagement.dto.request.ArchiveProjectRequest;
import com.example.projectManagement.dto.request.CreateProjectRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService=projectService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse> createProject(@Valid @RequestBody CreateProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.projectService.createProject(request));
    }

    @PostMapping("/archive")
    public ResponseEntity<StandardResponse> archiveProject(@Valid @RequestBody ArchiveProjectRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(this.projectService.archiveProject(request));
    }
}
