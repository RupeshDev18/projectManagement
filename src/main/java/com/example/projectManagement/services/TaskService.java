package com.example.projectManagement.services;


import com.example.projectManagement.dto.request.ChangeTaskStatusRequest;
import com.example.projectManagement.dto.request.CreateTaskRequest;
import com.example.projectManagement.dto.response.StandardResponse;
import com.example.projectManagement.entity.*;
import com.example.projectManagement.enums.TaskStatus;
import com.example.projectManagement.repositories.ProjectRepositories;
import com.example.projectManagement.repositories.TaskRepositories;
import com.example.projectManagement.repositories.UserRepositories;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class TaskService {
    private final WorkspaceService workspaceService;
    private final ProjectRepositories projectRepositories;
    private final UserRepositories userRepositories;
    private final TaskRepositories taskRepositories;
    private final AuditLogService auditLogService;

    public TaskService(WorkspaceService workspaceService,
                       ProjectRepositories projectRepositories,
                       TaskRepositories taskRepositories,
                       UserRepositories userRepositories,
                       AuditLogService auditLogService) {
        this.workspaceService = workspaceService;
        this.projectRepositories= projectRepositories;
        this.taskRepositories = taskRepositories;
        this.userRepositories=userRepositories;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public StandardResponse createTask(CreateTaskRequest request){
        User currUser=this.workspaceService.getCurrentUser();

        Project project=this.projectRepositories.findById(request.projectId()).orElseThrow(()->new RuntimeException("Project doesn't exist"));

        Long workspaceId=project.getWorkspace().getId();

        this.workspaceService.getMemberOrThrow(workspaceId, currUser.getId());

        User assignee=userRepositories.findById(request.assigneeId()).orElseThrow(()->new RuntimeException("Assignee doesn't exist"));

        this.workspaceService.getMemberOrThrow(workspaceId,assignee.getId());

        Task task=new Task();
        task.setStatus(TaskStatus.TODO);
        task.setDescription(request.description());
        task.setTitle(request.title());
        task.setCreatedBy(currUser);
        task.setAssignee(assignee);
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setProject(project);

        taskRepositories.save(task);

        return new StandardResponse("Task created successfully");
    }

    @Transactional
    public StandardResponse changeTaskStatus(ChangeTaskStatusRequest request){
        Task task = taskRepositories.findById(request.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskStatus current = task.getStatus();
        TaskStatus next = request.status();
        User currentUser=this.workspaceService.getCurrentUser();
        workspaceService.getMemberOrThrow(task.getProject().getWorkspace().getId(),currentUser.getId());

        if (current == next) {
            return new StandardResponse("Task is already in " + next + " state.");
        }

        validateTransition(current,next);

        task.setStatus(next);

        auditLogService.log(
                "Status changed from " + current + " to " + next,
                "TASK",
                task.getId(),
                currentUser
        );

        return new StandardResponse("Status changed successfully");
    }

    private void validateTransition(TaskStatus current,
                                    TaskStatus next){
        switch (current) {

            case TODO:
                if (next != TaskStatus.IN_PROGRESS) {
                    throw new RuntimeException("Invalid transition");
                }
                break;

            case IN_PROGRESS:
                if (next != TaskStatus.TODO &&
                        next != TaskStatus.IN_REVIEW) {

                    throw new RuntimeException("Invalid transition");
                }
                break;

            case IN_REVIEW:
                if (next != TaskStatus.DONE &&
                        next != TaskStatus.IN_PROGRESS) {

                    throw new RuntimeException("Invalid transition");
                }
                break;

            case DONE:
                throw new RuntimeException("Completed task cannot be modified");
        }
    }
}
