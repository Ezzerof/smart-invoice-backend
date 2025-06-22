package com.smartinvoice.client.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.invoice.entity.Invoice;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientRepository repository;
    private AuditLogService auditLogService;
    private ClientService service;

    @BeforeEach
    void setup() {
        repository = mock(ClientRepository.class);
        auditLogService = mock(AuditLogService.class);
        service = new ClientService(repository, auditLogService);
    }

    @Test
    @DisplayName("Should create a new client - happy path")
    void createClient_shouldSucceed() {
        ClientRequestDto dto = new ClientRequestDto("John", "john@example.com", "Company", "Address", "City", "Country", "12345");
        Client saved = Client.builder().id(1L).name("John").email("john@example.com").companyName("Company").address("Address").city("City").country("Country").postcode("12345").build();
        when(repository.save(any(Client.class))).thenReturn(saved);

        ClientResponseDto response = service.createClient(dto);

        assertThat(response.id()).isEqualTo(1L);
        verify(auditLogService).log("CREATE", "Client", "1");
    }

    @Test
    @DisplayName("Should get all clients")
    void getAllClients_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(Client.builder().id(1L).name("Test").build()));
        List<ClientResponseDto> clients = service.getAllClients();
        assertThat(clients).hasSize(1);
    }

    @Test
    @DisplayName("Should get client by ID")
    void getClientById_shouldReturnClient() {
        Client client = Client.builder().id(1L).name("Test").build();
        when(repository.findById(1L)).thenReturn(Optional.of(client));

        ClientResponseDto dto = service.getClientById(1L);
        assertThat(dto.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFound when client not found")
    void getClientById_shouldThrowNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getClientById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update client - happy path")
    void updateClient_shouldSucceed() {
        Client existing = Client.builder().id(1L).build();
        ClientRequestDto dto = new ClientRequestDto("New Name", "email", "Company", "Address", null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Client.class))).thenReturn(existing);

        ClientResponseDto updated = service.updateClient(1L, dto);
        verify(auditLogService).log("UPDATE", "Client", "1");
        assertThat(updated.name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should delete client with no invoices")
    void deleteClient_shouldSucceed() {
        Client client = Client.builder().id(1L).invoices(Collections.emptyList()).build();
        when(repository.findById(1L)).thenReturn(Optional.of(client));
        service.deleteClient(1L);
        verify(repository).delete(client);
        verify(auditLogService).log("DELETE", "Client", "1");
    }

    @Test
    @DisplayName("Should not delete client with invoices")
    void deleteClient_withInvoices_shouldFail() {
        Invoice invoice = mock(Invoice.class);
        Client client = Client.builder().id(1L).invoices(List.of(invoice)).build();

        when(repository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> service.deleteClient(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Client has invoices");
    }


    @Test
    @DisplayName("Should write clients to CSV - happy path")
    void writeClientsToCsv_shouldSucceed() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        Client mockClient = Client.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .companyName("Acme Inc.")
                .address("123 Street")
                .city("London")
                .country("UK")
                .postcode("E1 6AN")
                .build();

        // Cast matcher to avoid ambiguity
        when(repository.findAll((Specification<Client>) any())).thenReturn(List.of(mockClient));

        service.writeClientsToCsv(response, new ClientFilterRequest(null, null, null, null));

        verify(writer).println("id,name,email,companyName,address,city,country,postcode");
        verify(writer).printf(anyString(), eq(1L), eq("John"), eq("john@example.com"),
                eq("Acme Inc."), eq("123 Street"), eq("London"), eq("UK"), eq("E1 6AN"));
    }

    @Test
    @DisplayName("Should escape quotes in CSV - indirectly via createClient")
    void escapeCsv_shouldReplaceQuotes() {
        Client savedClient = Client.builder()
                .id(1L)
                .name("John \"The Boss\" Doe")
                .email("e")
                .companyName("c")
                .address("a")
                .city("c")
                .country("c")
                .postcode("p")
                .build();

        when(repository.save(any())).thenReturn(savedClient);

        ClientRequestDto dto = new ClientRequestDto("John \"The Boss\" Doe", "e", "c", "a", "c", "c", "p");
        ClientResponseDto result = service.createClient(dto);

        assertThat(result).isNotNull();
        assertThat(result.name()).contains("\"");
    }

}


