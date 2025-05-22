package com.smartinvoice.audit.controller;

import com.smartinvoice.audit.entity.AuditLog;
import com.smartinvoice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity
    ) {
        if (action != null && entity != null) {
            return auditLogRepository.findByActionAndEntityOrderByTimestampDesc(action, entity);
        } else if (action != null) {
            return auditLogRepository.findByActionOrderByTimestampDesc(action);
        } else if (entity != null) {
            return auditLogRepository.findByEntityOrderByTimestampDesc(entity);
        } else {
            return auditLogRepository.findAllByOrderByTimestampDesc();
        }
    }
}
