package com.smartinvoice.client.dto;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        double price,
        int quantity
) {
}
