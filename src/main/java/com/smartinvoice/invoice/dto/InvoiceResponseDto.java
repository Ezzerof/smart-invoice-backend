package com.smartinvoice.invoice.dto;

import com.smartinvoice.invoice.entity.Invoice;

import java.time.LocalDate;
import java.util.List;

public record InvoiceResponseDto(
        Long id,
        String clientName,
        String email,
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        double totalAmount,
        Long clientId,
        List<Long> productIds,
        Boolean isPaid,
        Invoice.InvoiceStatus status

) {}
