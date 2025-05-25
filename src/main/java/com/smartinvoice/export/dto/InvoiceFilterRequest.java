package com.smartinvoice.export.dto;

import java.time.LocalDate;

public record InvoiceFilterRequest(
        LocalDate issueDate,
        LocalDate dueDate,
        Long clientId,
        Boolean isPaid
) {}
