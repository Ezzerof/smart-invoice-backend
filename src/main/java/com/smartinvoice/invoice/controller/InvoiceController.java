package com.smartinvoice.invoice.controller;

import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDto createInvoice(@Valid @RequestBody InvoiceRequestDto dto) {
        return invoiceService.createInvoice(dto);
    }

    @GetMapping
    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceResponseDto getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }
}
