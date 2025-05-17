package com.smartinvoice.invoice.dto;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequestDto(
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        Long clientId,
        List<Long> productIds,
        Boolean isPaid
) {}
