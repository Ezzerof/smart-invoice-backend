package com.smartinvoice.export.controller;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.client.service.ClientService;
import com.smartinvoice.exception.GlobalExceptionHandler;
import com.smartinvoice.export.dto.ExportClientFilterRequest;
import com.smartinvoice.export.dto.InvoiceFilterRequest;
import com.smartinvoice.export.service.ExportService;
import com.smartinvoice.invoice.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExportControllerTest {

    private MockMvc mockMvc;
    private ClientService clientService;
    private InvoiceService invoiceService;
    private ExportService exportService;

    @BeforeEach
    void setup() {
        clientService = mock(ClientService.class);
        invoiceService = mock(InvoiceService.class);
        exportService = mock(ExportService.class);

        ExportController controller = new ExportController(clientService, invoiceService, exportService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should export clients with filters - happy path")
    void exportClientsToCsv_happy() throws Exception {
        doNothing().when(clientService).writeClientsToCsv(any(), any(ClientFilterRequest.class));
        when(exportService.mapToClientFilter(any(ExportClientFilterRequest.class)))
                .thenReturn(new ClientFilterRequest("John", "London", "UK", null));

        mockMvc.perform(get("/api/export/clients/csv")
                        .param("name", "John")
                        .param("city", "London")
                        .param("country", "UK"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should export invoices with valid dates - happy path")
    void exportInvoicesToCsv_happy() throws Exception {
        doNothing().when(invoiceService).exportInvoicesToCsv(any(), any(InvoiceFilterRequest.class));
        doNothing().when(exportService).validateInvoiceDates(any(), any());

        mockMvc.perform(get("/api/export/invoices/csv")
                        .param("issueDate", "2024-01-01")
                        .param("dueDate", "2024-02-01"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 when issue date is after due date - unhappy path")
    void exportInvoicesToCsv_invalidDateRange() throws Exception {
        doThrow(new IllegalArgumentException("Start date must be before end date"))
                .when(exportService).validateInvoiceDates(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 1, 1));

        mockMvc.perform(get("/api/export/invoices/csv")
                        .param("issueDate", "2024-02-01")
                        .param("dueDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should export invoices with no filters - edge case")
    void exportInvoicesToCsv_noFilters() throws Exception {
        doNothing().when(invoiceService).exportInvoicesToCsv(any(), any(InvoiceFilterRequest.class));
        doNothing().when(exportService).validateInvoiceDates(any(), any());

        mockMvc.perform(get("/api/export/invoices/csv"))
                .andExpect(status().isOk());
    }
}
