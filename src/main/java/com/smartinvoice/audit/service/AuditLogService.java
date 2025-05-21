package com.smartinvoice.audit.service;

import com.smartinvoice.audit.entity.AuditLog;
import com.smartinvoice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entity, String entityId) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}
