package com.smartinvoice.client.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
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

        return mapToDto(saved);
    }

    // Get a list of all clients
    public List<ClientResponseDto> getAllClients() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get a client by ID
    public ClientResponseDto getClientById(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        return mapToDto(client);
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

        return mapToDto(updatedClient);
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

    public void writeClientsToCsv(HttpServletResponse response) throws IOException {
        List<ClientResponseDto> clients = getAllClients();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("id,name,email,companyName,address,city,country,postcode");

            for (ClientResponseDto client : clients) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                        client.id(),
                        client.name(),
                        client.email(),
                        client.companyName(),
                        client.address(),
                        client.city(),
                        client.country(),
                        client.postcode()
                );
            }
        }
    }

    private ClientResponseDto mapToDto(Client client) {
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
}
