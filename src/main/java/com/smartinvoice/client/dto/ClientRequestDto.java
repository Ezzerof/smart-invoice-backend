package com.smartinvoice.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequestDto(
        @NotBlank String name,
        @NotBlank @Email String email,
        String companyName,
        String address
) {}
