package com.smartinvoice.audit.controller;

import com.smartinvoice.audit.entity.AuditLog;
import com.smartinvoice.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AuditLogControllerTest {

    private MockMvc mockMvc;
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository = mock(AuditLogRepository.class);
        AuditLogController controller = new AuditLogController(auditLogRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Should return all audit logs")
    void getAllLogs() throws Exception {
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of(createLog("CREATE", "Client")));

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("CREATE"));
    }

    @Test
    @DisplayName("Should return logs by action")
    void getLogsByAction() throws Exception {
        when(auditLogRepository.findByActionOrderByTimestampDesc("CREATE"))
                .thenReturn(List.of(createLog("CREATE", "Client")));

        mockMvc.perform(get("/api/audit-logs").param("action", "CREATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entity").value("Client"));
    }

    @Test
    @DisplayName("Should return logs by entity")
    void getLogsByEntity() throws Exception {
        when(auditLogRepository.findByEntityOrderByTimestampDesc("Client"))
                .thenReturn(List.of(createLog("DELETE", "Client")));

        mockMvc.perform(get("/api/audit-logs").param("entity", "Client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("DELETE"));
    }

    @Test
    @DisplayName("Should return logs by action and entity")
    void getLogsByActionAndEntity() throws Exception {
        when(auditLogRepository.findByActionAndEntityOrderByTimestampDesc("UPDATE", "Client"))
                .thenReturn(List.of(createLog("UPDATE", "Client")));

        mockMvc.perform(get("/api/audit-logs")
                        .param("action", "UPDATE")
                        .param("entity", "Client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("UPDATE"));
    }

    private AuditLog createLog(String action, String entity) {
        return AuditLog.builder()
                .id(1L)
                .action(action)
                .entity(entity)
                .entityId("123")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

