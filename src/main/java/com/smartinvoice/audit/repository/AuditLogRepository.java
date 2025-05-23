package com.smartinvoice.audit.repository;

import com.smartinvoice.audit.entity.AuditLog;
import org.springdoc.core.converters.models.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    List<AuditLog> findByEntityOrderByTimestampDesc(String entity);
    List<AuditLog> findByActionAndEntityOrderByTimestampDesc(String action, String entity);
    List<AuditLog> findAllByOrderByTimestampDesc();
}

