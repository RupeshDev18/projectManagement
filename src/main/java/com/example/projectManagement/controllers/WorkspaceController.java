package com.example.projectManagement.controllers;


import com.example.projectManagement.dto.request.CreateWorkSpaceRequest;
import com.example.projectManagement.dto.request.InviteMemberRequest;
import com.example.projectManagement.dto.response.CreateWorkSpaceResponse;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.dto.response.WorkspaceResponse;
import com.example.projectManagement.services.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspace(){
        return ResponseEntity.status(200).body(this.workspaceService.getMyWorkspaces());
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<StandardResponse> inviteMemberToWorkspace(@RequestBody InviteMemberRequest request,@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.workspaceService.inviteMember(request,id));
    }

    @DeleteMapping("/{id}/members/{email}")
    public ResponseEntity<StandardResponse> removeMemberFromWorkspace(@PathVariable String email,@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.workspaceService.removeMember(email,id));
    }



}
