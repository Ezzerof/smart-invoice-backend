package com.smartinvoice.export.dto;

public record ExportClientFilterRequest(
        String name,
        String companyName,
        String city,
        String country
) {}
