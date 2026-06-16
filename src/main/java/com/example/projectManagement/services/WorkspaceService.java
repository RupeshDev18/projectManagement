package com.example.projectManagement.services;


import com.example.projectManagement.dto.request.CreateWorkSpaceRequest;
import com.example.projectManagement.dto.response.CreateWorkSpaceResponse;
import com.example.projectManagement.dto.response.WorkspaceResponse;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.entity.Workspace;
import com.example.projectManagement.entity.WorkspaceMember;
import com.example.projectManagement.enums.WorkspaceRole;
import com.example.projectManagement.repositories.UserRepositories;
import com.example.projectManagement.repositories.WorkspaceMemberRepositories;
import com.example.projectManagement.repositories.WorkspaceRepositories;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class WorkspaceService {

    private final WorkspaceMemberRepositories workspaceMemberRepositories;
    private final WorkspaceRepositories workspaceRepositories;
    private final UserRepositories userRepositories;

    public WorkspaceService(WorkspaceMemberRepositories workspaceMemberRepositories, WorkspaceRepositories workspaceRepositories, UserRepositories userRepositories){
        this.workspaceMemberRepositories=workspaceMemberRepositories;
        this.workspaceRepositories=workspaceRepositories;
        this.userRepositories = userRepositories;
    }

    private User getCurrentUser(){

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
//        return userDetails.getUser();
        String email= auth.getName();

        User user=userRepositories.findByEmail(email).orElseThrow(()-> new RuntimeException("user not found"));
        return user;
    }

    public List<WorkspaceResponse> getMyWorkspaces(){
        User user=getCurrentUser();
        workspaceMemberRepositories.findAllByUserId(user.getId());

    }


    @Transactional
    public CreateWorkSpaceResponse createWorkspace(CreateWorkSpaceRequest request){
        User user=getCurrentUser();
        Workspace workspace=new Workspace();
        workspace.setName(request.name());
        workspace.setDescription(request.description());
        workspace.setOwner(user);

        workspaceRepositories.save(workspace);
        WorkspaceMember workspaceMember=new WorkspaceMember();
        workspaceMember.setWorkspace(workspace);
        workspaceMember.setUser(user);
        workspaceMember.setRole(WorkspaceRole.OWNER);

        workspaceMemberRepositories.save(workspaceMember);

        return new CreateWorkSpaceResponse("Workspace Created Successfully.");
    }
}
