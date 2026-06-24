package com.example.projectManagement.services;


import com.example.projectManagement.dto.request.CreateWorkSpaceRequest;
import com.example.projectManagement.dto.request.InviteMemberRequest;
import com.example.projectManagement.dto.request.RemoveMemberRequest;
import com.example.projectManagement.dto.response.CreateWorkSpaceResponse;
import com.example.projectManagement.dto.response.StandardResponse;
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
import java.util.Optional;


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
        List<WorkspaceMember> workspaceMember=workspaceMemberRepositories.findAllByUserId(user.getId());
        return workspaceMember.stream().map((member)->
        {
            Workspace workspace=member.getWorkspace();
            return new WorkspaceResponse(
                    workspace.getId(),workspace.getName(),workspace.getDescription(),member.getRole()
            );
        }).toList();

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

    @Transactional
    public StandardResponse inviteMember(InviteMemberRequest request,long workspaceId){

        Workspace workspace=workspaceRepositories.findById(workspaceId).orElseThrow(()-> new RuntimeException("Workspace doesn't exists"));

        User user=userRepositories.findByEmail(request.email()).orElseThrow(()-> new RuntimeException("User doesn't exists"));

        User currUser=getCurrentUser();
        WorkspaceMember member=getMemberOrThrow(workspaceId, currUser.getId());
        if(member.getRole()!=WorkspaceRole.OWNER){
            throw new RuntimeException("Only Owner can invite members.");
        }

        if(workspaceMemberRepositories.existsByUserIdAndWorkspaceId(user.getId(),workspace.getId())){
            throw new RuntimeException("member already in workspace");
        }
        WorkspaceMember workspaceMember=new WorkspaceMember();
        workspaceMember.setUser(user);
        workspaceMember.setWorkspace(workspace);
        workspaceMember.setRole(WorkspaceRole.MEMBER);

        workspaceMemberRepositories.save(workspaceMember);

        return new StandardResponse("member invited successfully");

    }

    @Transactional
    public StandardResponse removeMember(String email,long workspaceId){
        User currUser=getCurrentUser();
        WorkspaceMember workspaceMember=getMemberOrThrow(workspaceId, currUser.getId());
        if(workspaceMember.getRole()!=WorkspaceRole.OWNER){
            throw new RuntimeException("Only Owners can remove members");
        }
        User user=userRepositories.findByEmail(email).orElseThrow(()-> new RuntimeException("User doesn't exists"));

        if(!workspaceMemberRepositories.existsByUserIdAndWorkspaceId(user.getId(), workspaceId)){
            throw new RuntimeException("User is not in current workspace");
        }
        WorkspaceMember targetMember=getMemberOrThrow(workspaceId, user.getId());
        if(targetMember.getRole()==WorkspaceRole.OWNER){
            throw new RuntimeException("Can't remove Owners");
        }
        workspaceMemberRepositories.delete(targetMember);

        return new StandardResponse("member removed successfully");
    }

    private WorkspaceMember getMemberOrThrow(Long workspaceId,Long userId){
        return workspaceMemberRepositories.findByUserIdAndWorkspaceId(userId,workspaceId).orElseThrow(()->new RuntimeException("member doesn't exist."));
    }

}