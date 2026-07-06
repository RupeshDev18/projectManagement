package com.example.projectManagement.services;


import com.example.projectManagement.dto.request.CreateTaskRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.dto.response.WorkspaceResponse;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.entity.Workspace;
import com.example.projectManagement.repositories.ProjectRepositories;
import com.example.projectManagement.repositories.TaskRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final WorkspaceService workspaceService;
    private final ProjectRepositories projectRepositories;
    private final TaskRepositories taskRepositories;

    public TaskService(WorkspaceService workspaceService
            ,ProjectRepositories projectRepositories,
                          TaskRepositories taskRepositories) {
        this.workspaceService = workspaceService;
        this.projectRepositories= projectRepositories;
        this.taskRepositories = taskRepositories;
    }

    public StandardResponse createTask(CreateTaskRequest request){
        User currUser=this.workspaceService.getCurrentUser();
        List<WorkspaceResponse> workspace=this.workspaceService.getMyWorkspaces();
        return new StandardResponse("Task created successfully");
    }
}
