package com.smartinvoice.export.dto;

public record ClientFilterRequest(
        String name,
        String companyName,
        String city,
        String country
) {}
