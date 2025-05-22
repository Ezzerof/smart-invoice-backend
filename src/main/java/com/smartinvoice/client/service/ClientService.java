package com.smartinvoice.client.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final AuditLogService auditLogService;

    // Create a new client
    public ClientResponseDto createClient(ClientRequestDto dto) {
        Client client = Client.builder()
                .name(dto.name())
                .email(dto.email())
                .companyName(dto.companyName())
                .address(dto.address())
                .city(dto.city())
                .country(dto.country())
                .postcode(dto.postcode())
                .build();

        Client saved = repository.save(client);

        auditLogService.log("CREATE", "Client", String.valueOf(saved.getId()));

        return new ClientResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCompanyName(),
                saved.getAddress(),
                saved.getCity(),
                saved.getCountry(),
                saved.getPostcode()
        );
    }

    // Get a list of all clients
    public List<ClientResponseDto> getAllClients() {
        return repository.findAll().stream()
                .map(client -> new ClientResponseDto(
                        client.getId(),
                        client.getName(),
                        client.getEmail(),
                        client.getCompanyName(),
                        client.getAddress(),
                        client.getCity(),
                        client.getCountry(),
                        client.getPostcode()))
                .collect(Collectors.toList());
    }

    // Get a client by ID
    public ClientResponseDto getClientById(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        return new ClientResponseDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getCompanyName(),
                client.getAddress(),
                client.getCity(),
                client.getCountry(),
                client.getPostcode()
        );
    }

    // Update a client
    public ClientResponseDto updateClient(Long id, ClientRequestDto dto) {
        Client existingClient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        existingClient.setName(dto.name());
        existingClient.setEmail(dto.email());
        existingClient.setCompanyName(dto.companyName());
        existingClient.setAddress(dto.address());

        Client updatedClient = repository.save(existingClient);

        auditLogService.log("UPDATE", "Client", String.valueOf(updatedClient.getId()));

        return new ClientResponseDto(
                updatedClient.getId(),
                updatedClient.getName(),
                updatedClient.getEmail(),
                updatedClient.getCompanyName(),
                updatedClient.getAddress(),
                updatedClient.getCity(),
                updatedClient.getCountry(),
                updatedClient.getPostcode()
        );
    }

    // Delete a client
    public void deleteClient(Long id) {
        Client existingClient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        if (!existingClient.getInvoices().isEmpty()) {
            throw new IllegalStateException("Client has invoices and cannot be deleted.");
        }

        repository.delete(existingClient);

        auditLogService.log("DELETE", "Client", String.valueOf(existingClient.getId()));
    }
}
