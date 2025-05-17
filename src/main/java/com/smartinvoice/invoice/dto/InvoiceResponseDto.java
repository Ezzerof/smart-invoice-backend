package com.smartinvoice.invoice.dto;

import java.time.LocalDate;
import java.util.List;

public record InvoiceResponseDto(
        Long id,
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        double totalAmount,
        Long clientId,
        List<Long> productIds,
        Boolean isPaid
) {}
