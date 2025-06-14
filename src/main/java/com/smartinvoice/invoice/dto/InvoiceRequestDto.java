package com.smartinvoice.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequestDto(
        Long clientId,
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        Boolean isPaid,
        @NotNull
        @Valid
        List<@Valid ProductDto> products
) {
}
