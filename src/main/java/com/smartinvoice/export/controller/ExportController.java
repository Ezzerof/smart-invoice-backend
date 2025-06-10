package com.smartinvoice.export.controller;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.export.dto.ExportClientFilterRequest;
import com.smartinvoice.export.dto.InvoiceFilterRequest;
import com.smartinvoice.client.service.ClientService;
import com.smartinvoice.invoice.service.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ClientService clientService;
    private final InvoiceService invoiceService;

    @GetMapping("/clients/csv")
    public void exportClientsToCsv(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            HttpServletResponse response) throws IOException {

        // Set headers
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"clients_export.csv\"");

        // Map ExportClientFilterRequest to ClientFilterRequest
        ExportClientFilterRequest exportFilters = new ExportClientFilterRequest(name, companyName, city, country);

        // Keyword = combination of name + company name
        String keyword = buildKeywordFrom(exportFilters.name(), exportFilters.companyName());

        // Create a compatible ClientFilterRequest with keyword
        ClientFilterRequest adapted = new ClientFilterRequest(
                keyword,
                exportFilters.city(),
                exportFilters.country(),
                null
        );

        clientService.writeClientsToCsv(response, adapted);
    }

    private String buildKeywordFrom(String name, String companyName) {
        if (name == null && companyName == null) return null;
        StringBuilder sb = new StringBuilder();
        if (name != null) sb.append(name).append(" ");
        if (companyName != null) sb.append(companyName);
        return sb.toString().trim();
    }

    @GetMapping("/invoices/csv")
    public void exportInvoicesToCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Boolean isPaid,
            HttpServletResponse response) throws IOException {

        // Validate date range
        if (issueDate != null && dueDate != null && issueDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"invoices_export.csv\"");

        // Build filters and export
        InvoiceFilterRequest filters = new InvoiceFilterRequest(issueDate, dueDate, clientId, isPaid);
        invoiceService.exportInvoicesToCsv(response, filters);
    }
}
