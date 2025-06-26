package com.smartinvoice.audit.service;

import com.smartinvoice.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AuditLogServiceTest {

    private AuditLogRepository repository;
    private AuditLogService service;

    @BeforeEach
    void setup() {
        repository = mock(AuditLogRepository.class);
        service = new AuditLogService(repository);
    }

    @Test
    @DisplayName("Should create and save an audit log entry")
    void log_shouldSaveAuditEntry() {
        // Given
        String action = "CREATE";
        String entity = "Client";
        String entityId = "123";

        // When
        service.log(action, entity, entityId);

        // Then
        verify(repository, times(1)).save(argThat(log ->
                log.getAction().equals(action) &&
                        log.getEntity().equals(entity) &&
                        log.getEntityId().equals(entityId) &&
                        log.getTimestamp() != null
        ));
    }
}
