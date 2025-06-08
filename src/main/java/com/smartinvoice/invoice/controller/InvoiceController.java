package com.smartinvoice.invoice.controller;

import com.smartinvoice.invoice.dto.InvoiceSearchFilter;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import com.smartinvoice.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDto createInvoice(@Valid @RequestBody InvoiceRequestDto dto) {
        return invoiceService.createInvoice(dto);
    }

    @GetMapping
    public List<InvoiceResponseDto> getAllInvoices(
            @ModelAttribute InvoiceSearchFilter filter
    ) {
        return invoiceService.getFilteredInvoices(filter);
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

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.getInvoicePdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{id}/email")
    @ResponseStatus(HttpStatus.OK)
    public void emailInvoiceToClient(@PathVariable Long id) {
        invoiceService.emailInvoiceToClient(id);
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
        return ResponseEntity.ok().build();
    }
}
