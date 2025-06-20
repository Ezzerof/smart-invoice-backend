package com.smartinvoice.client.controller;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // Create a new client
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDto create(@Valid @RequestBody ClientRequestDto dto) {
        return clientService.createClient(dto);
    }

    // Get a list of all clients
    @GetMapping
    public List<ClientResponseDto> getClients(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortBy
    ) {
        ClientFilterRequest filters = new ClientFilterRequest(keyword, city, country, sortBy);
        return clientService.getFilteredClients(filters);
    }

    @GetMapping("/filter")
    public List<ClientResponseDto> getFilteredClients(ClientFilterRequest filters) {
        return clientService.getFilteredClients(filters);
    }


    // Get a single client by ID
    @GetMapping("/{id}")
    public ClientResponseDto getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    // Update client information
    @PutMapping("/{id}")
    public ClientResponseDto updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequestDto dto) {
        return clientService.updateClient(id, dto);
    }

    // Delete a client by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
