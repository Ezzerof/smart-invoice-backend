package com.smartinvoice.client.service;

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

    // Create a new client
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

    // Get a list of all clients
    public List<ClientResponseDto> getAllClients() {
        return repository.findAll().stream()
                .map(client -> new ClientResponseDto(
                        client.getId(),
                        client.getName(),
                        client.getEmail(),
                        client.getCompanyName(),
                        client.getAddress()))
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
                client.getAddress()
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

        return new ClientResponseDto(
                updatedClient.getId(),
                updatedClient.getName(),
                updatedClient.getEmail(),
                updatedClient.getCompanyName(),
                updatedClient.getAddress()
        );
    }

    // Delete a client
    public void deleteClient(Long id) {
        Client existingClient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        repository.delete(existingClient);
    }
}
