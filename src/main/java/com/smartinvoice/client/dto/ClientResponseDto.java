package com.smartinvoice.client.dto;

public record ClientResponseDto(
        Long id,
        String name,
        String email,
        String companyName,
        String address,
        String city,
        String country,
        String postcode
) {}
