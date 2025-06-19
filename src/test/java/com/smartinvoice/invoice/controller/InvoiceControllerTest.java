package com.smartinvoice.invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.dto.InvoiceSearchFilter;
import com.smartinvoice.invoice.dto.ProductDto;
import com.smartinvoice.invoice.service.InvoiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceService invoiceService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public InvoiceService invoiceService() {
            return Mockito.mock(InvoiceService.class);
        }

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Test
    @WithMockUser
    @DisplayName("Should create invoice successfully")
    void shouldCreateInvoiceSuccessfully() throws Exception {
        InvoiceRequestDto request = new InvoiceRequestDto(1L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), false,
                List.of(new ProductDto(1L, 2, 100.0)));
        InvoiceResponseDto response = new InvoiceResponseDto(1L, "Alice", "alice@mail.com", "INV-001",
                LocalDate.now(), LocalDate.now().plusDays(7), 200.0, 1L, List.of(1L), false, null);

        Mockito.when(invoiceService.createInvoice(any())).thenReturn(response);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-001"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 on invalid create request")
    void shouldReturn400OnInvalidCreate() throws Exception {
        InvoiceRequestDto invalid = new InvoiceRequestDto(1L, "", null, null, null, null);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get invoice by ID")
    void shouldGetInvoiceById() throws Exception {
        InvoiceResponseDto response = new InvoiceResponseDto(1L, "Alice", "alice@mail.com", "INV-001",
                LocalDate.now(), LocalDate.now().plusDays(7), 200.0, 1L, List.of(1L), false, null);

        Mockito.when(invoiceService.getInvoiceById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-001"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete invoice")
    void shouldDeleteInvoice() throws Exception {
        mockMvc.perform(delete("/api/invoices/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(invoiceService).deleteInvoice(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should mark invoice as paid")
    void shouldMarkAsPaid() throws Exception {
        mockMvc.perform(patch("/api/invoices/1/mark-paid"))
                .andExpect(status().isOk());

        Mockito.verify(invoiceService).markAsPaid(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get invoice PDF")
    void shouldReturnInvoicePdf() throws Exception {
        byte[] pdfBytes = new byte[]{1, 2, 3};
        Mockito.when(invoiceService.getInvoicePdf(1L)).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/invoices/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @WithMockUser
    @DisplayName("Should email invoice")
    void shouldEmailInvoiceToClient() throws Exception {
        mockMvc.perform(post("/api/invoices/1/email"))
                .andExpect(status().isOk());

        Mockito.verify(invoiceService).emailInvoiceToClient(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should filter invoices")
    void shouldFilterInvoices() throws Exception {
        InvoiceResponseDto invoice = new InvoiceResponseDto(1L, "Alice", "alice@mail.com", "INV-001",
                LocalDate.now(), LocalDate.now().plusDays(7), 200.0, 1L, List.of(1L), false, null);
        Mockito.when(invoiceService.getFilteredInvoices(any(InvoiceSearchFilter.class))).thenReturn(List.of(invoice));

        mockMvc.perform(get("/api/invoices").param("search", "INV-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-001"));
    }
}
