package com.example.projectManagement.services;

import com.example.projectManagement.entity.AuditLog;
import com.example.projectManagement.entity.User;
import com.example.projectManagement.repositories.AuditLogRepositories;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepositories auditLogRepositories;

    public AuditLogService(AuditLogRepositories auditLogRepositories){
        this.auditLogRepositories =auditLogRepositories;
    }

        public void log(String action,
                        String entityType,
                        Long entityId,
                        User user) {

            AuditLog log = new AuditLog();

            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setPerformedBy(user);

            auditLogRepositories.save(log);
        }

}
