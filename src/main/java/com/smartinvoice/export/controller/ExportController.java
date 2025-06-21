package com.smartinvoice.export.controller;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.client.service.ClientService;
import com.smartinvoice.export.dto.ExportClientFilterRequest;
import com.smartinvoice.export.dto.InvoiceFilterRequest;
import com.smartinvoice.export.service.ExportService;
import com.smartinvoice.invoice.service.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ClientService clientService;
    private final InvoiceService invoiceService;
    private final ExportService exportService;

    @GetMapping("/clients/csv")
    public void exportClientsToCsv(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            HttpServletResponse response) throws IOException {

        ExportClientFilterRequest exportFilters = new ExportClientFilterRequest(name, companyName, city, country);
        ClientFilterRequest adapted = exportService.mapToClientFilter(exportFilters);
        clientService.writeClientsToCsv(response, adapted);
    }

    @GetMapping("/invoices/csv")
    public void exportInvoicesToCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Boolean isPaid,
            HttpServletResponse response) throws IOException {

        exportService.validateInvoiceDates(issueDate, dueDate);
        InvoiceFilterRequest filters = new InvoiceFilterRequest(issueDate, dueDate, clientId, isPaid);
        invoiceService.exportInvoicesToCsv(response, filters);
    }
}
