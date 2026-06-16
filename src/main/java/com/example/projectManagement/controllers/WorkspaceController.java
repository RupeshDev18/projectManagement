package com.example.projectManagement.controllers;


import com.example.projectManagement.dto.request.CreateWorkSpaceRequest;
import com.example.projectManagement.dto.response.CreateWorkSpaceResponse;
import com.example.projectManagement.services.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService){
        this.workspaceService=workspaceService;
    }


    @PostMapping
    public ResponseEntity<CreateWorkSpaceResponse> createWorkspace(@Valid  @RequestBody CreateWorkSpaceRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.workspaceService.createWorkspace(request));
    }

}
