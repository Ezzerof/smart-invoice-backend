package com.smartinvoice.invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.service.InvoiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InvoiceController.class)
@Import(InvoiceControllerTest.Config.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceService invoiceService;

    @TestConfiguration
    static class Config {
        @Bean
        public InvoiceService invoiceService() {
            return Mockito.mock(InvoiceService.class);
        }

        @Bean
        public InvoiceController invoiceController(InvoiceService invoiceService) {
            return new InvoiceController(invoiceService);
        }
    }


    @Test
    @DisplayName("POST /api/invoices - should create invoice")
    void shouldCreateInvoice() throws Exception {
        InvoiceRequestDto request = new InvoiceRequestDto("INV-001", LocalDate.now(), LocalDate.now().plusDays(7), 1L, List.of(1L, 2L));
        InvoiceResponseDto response = new InvoiceResponseDto(1L, request.invoiceNumber(), request.issueDate(), request.dueDate(), 300.0, 1L, List.of(1L, 2L));

        Mockito.when(invoiceService.createInvoice(any())).thenReturn(response);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/invoices - should return all invoices")
    void shouldGetAllInvoices() throws Exception {
        InvoiceResponseDto invoice = new InvoiceResponseDto(1L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), 150.0, 1L, List.of(1L));

        Mockito.when(invoiceService.getAllInvoices()).thenReturn(List.of(invoice));

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-001"));
    }

    @Test
    @DisplayName("GET /api/invoices/{id} - should return invoice by ID")
    void shouldGetInvoiceById() throws Exception {
        long id = 1L;
        InvoiceResponseDto invoice = new InvoiceResponseDto(id, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), 150.0, 1L, List.of(1L));

        Mockito.when(invoiceService.getInvoiceById(id)).thenReturn(invoice);

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-001"));
    }

    @Test
    @DisplayName("DELETE /api/invoices/{id} - should delete invoice")
    void shouldDeleteInvoice() throws Exception {
        long id = 1L;
        Mockito.doNothing().when(invoiceService).deleteInvoice(id);

        mockMvc.perform(delete("/api/invoices/{id}", id))
                .andExpect(status().isNoContent());
    }

}
