package com.smartinvoice.invoice.dto;

public record ProductDto(
        Long productId,
        int quantity,
        double price
) {}
