package com.smartinvoice.client.dto;

public record ClientFilterRequest(
        String keyword,
        String city,
        String country,
        String sortBy
) {}
