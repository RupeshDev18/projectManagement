package com.example.projectManagement.repositories;

import com.example.projectManagement.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepositories extends JpaRepository<AuditLog,Long> {

}
