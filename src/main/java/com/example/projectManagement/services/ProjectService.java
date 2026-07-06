package com.example.projectManagement.services;


import com.example.projectManagement.dto.request.ArchiveProjectRequest;
import com.example.projectManagement.dto.request.CreateProjectRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.entity.Project;
import com.example.projectManagement.entity.Task;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.entity.WorkspaceMember;
import com.example.projectManagement.enums.ProjectStatus;
import com.example.projectManagement.enums.TaskStatus;
import com.example.projectManagement.enums.WorkspaceRole;
import com.example.projectManagement.repositories.ProjectRepositories;
import com.example.projectManagement.repositories.TaskRepositories;
import com.example.projectManagement.repositories.UserRepositories;
import com.example.projectManagement.repositories.WorkspaceRepositories;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectService {

    private final WorkspaceService workspaceService;
    private final ProjectRepositories projectRepositories;
    private final TaskRepositories taskRepositories;

    public ProjectService(WorkspaceService workspaceService
                          ,ProjectRepositories projectRepositories,
                          TaskRepositories taskRepositories) {
        this.workspaceService = workspaceService;
        this.projectRepositories= projectRepositories;
        this.taskRepositories = taskRepositories;
    }

    public StandardResponse createProject(CreateProjectRequest request){
        User user=this.workspaceService.getCurrentUser();
        WorkspaceMember workspaceMember=this.workspaceService.getMemberOrThrow(request.workspaceId(), user.getId());
        if(workspaceMember.getRole()!= WorkspaceRole.OWNER && workspaceMember.getRole()!= WorkspaceRole.MANAGER){
            throw new RuntimeException("Only Owner or Manager can create a Project");
        }
        Project project=new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setWorkspace(workspaceMember.getWorkspace());
        projectRepositories.save(project);

        return new StandardResponse("Project created successfully.");
    }

    @Transactional
    public StandardResponse archiveProject(ArchiveProjectRequest request){
        User user=workspaceService.getCurrentUser();
        WorkspaceMember workspaceMember=workspaceService.getMemberOrThrow(request.workspaceId(), user.getId());

        if(workspaceMember.getRole()!=WorkspaceRole.OWNER && workspaceMember.getRole()!=WorkspaceRole.MANAGER){
            throw new RuntimeException("Only Owner and Manager can make project archive");
        }
        Project project=projectRepositories.findById(request.projectId()).orElseThrow(()->new RuntimeException("Project not found."));
        if(!project.getWorkspace().getId().equals(request.workspaceId())){
            throw new RuntimeException("Project doesn't belong to this workspace.");
        }

        List<Task> tasks=taskRepositories.findAllByProjectIdAndStatus(request.projectId(), TaskStatus.IN_PROGRESS);

        for(Task task:tasks){
            task.setStatus(TaskStatus.PAUSED);
//        taskRepositories.save(task)
        }

        project.setStatus(ProjectStatus.ARCHIVED);
//        projectRepositories.save(project);

        return new StandardResponse("Project archived successfully.");
    }
}
