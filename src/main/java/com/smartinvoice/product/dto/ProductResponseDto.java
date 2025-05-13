package com.smartinvoice.product.dto;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        double price,
        int quantity
) {
}
