package com.smartinvoice.client.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.export.dto.ClientFilterRequest;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

    public List<ClientResponseDto> getFilteredClients(ClientFilterRequest filters) {
        return repository.findAll(withFilters(filters)).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Specification<Client> withFilters(ClientFilterRequest filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.name() != null && !filters.name().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filters.name().toLowerCase() + "%"));
            }
            if (filters.companyName() != null && !filters.companyName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("companyName")), "%" + filters.companyName().toLowerCase() + "%"));
            }
            if (filters.city() != null && !filters.city().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), filters.city().toLowerCase()));
            }
            if (filters.country() != null && !filters.country().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("country")), filters.country().toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public void writeClientsToCsv(HttpServletResponse response, ClientFilterRequest filters) throws IOException {
        List<Client> clients = repository.findAll(withFilters(filters));

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=clients.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("id,name,email,companyName,address,city,country,postcode");

            clients.forEach(client -> writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    client.getId(),
                    escapeCsv(client.getName()),
                    escapeCsv(client.getEmail()),
                    escapeCsv(client.getCompanyName()),
                    escapeCsv(client.getAddress()),
                    escapeCsv(client.getCity()),
                    escapeCsv(client.getCountry()),
                    escapeCsv(client.getPostcode())
            ));
        }
    }

    private String escapeCsv(String input) {
        return input != null ? input.replace("\"", "\"\"") : "";
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
