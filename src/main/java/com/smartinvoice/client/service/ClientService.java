package com.smartinvoice.client.service;

import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public ClientResponseDto createClient(ClientRequestDto dto) {
        Client client = Client.builder()
                .name(dto.name())
                .email(dto.email())
                .companyName(dto.companyName())
                .address(dto.address())
                .build();

        Client saved = repository.save(client);

        return new ClientResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCompanyName(),
                saved.getAddress()
        );
    }
}
