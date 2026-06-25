package com.example.projectManagement.controllers;


import com.example.projectManagement.dto.request.CreateProjectRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @PostMapping
    public ResponseEntity<StandardResponse> createProject(@RequestBody CreateProjectRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(new StandardResponse("Project created successfully."));

    }
}
