package com.smartinvoice.product.dto;

public record ProductFilterRequest(
        String keyword,
        String sortBy // name, -name, price, -price
) {}
