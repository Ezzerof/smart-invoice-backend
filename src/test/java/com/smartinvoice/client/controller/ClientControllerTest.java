package com.smartinvoice.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.client.dto.ClientRequestDto;
import com.smartinvoice.client.dto.ClientResponseDto;
import com.smartinvoice.client.service.ClientService;
import com.smartinvoice.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientControllerTest {

    private MockMvc mockMvc;
    private ClientService clientService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clientService = mock(ClientService.class);
        objectMapper = new ObjectMapper();
        ClientController controller = new ClientController(clientService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should create a new client")
    void createClient_shouldSucceed() throws Exception {
        ClientRequestDto dto = new ClientRequestDto("John", "john@example.com", "Company", "Address", "City", "Country", "12345");
        ClientResponseDto response = new ClientResponseDto(1L, "John", "john@example.com", "Company", "Address", "City", "Country", "12345");
        when(clientService.createClient(any())).thenReturn(response);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DisplayName("Should return all clients with filters")
    void getClients_shouldReturnList() throws Exception {
        when(clientService.getFilteredClients(any(ClientFilterRequest.class)))
                .thenReturn(List.of(new ClientResponseDto(1L, "John", "john@example.com", null, null, null, null, null)));

        mockMvc.perform(get("/api/clients")
                        .param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    @DisplayName("Should get a client by ID")
    void getClientById_shouldReturnClient() throws Exception {
        when(clientService.getClientById(1L))
                .thenReturn(new ClientResponseDto(1L, "John", "john@example.com", null, null, null, null, null));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Should update client")
    void updateClient_shouldSucceed() throws Exception {
        ClientRequestDto dto = new ClientRequestDto("John", "john@example.com", "Company", "Address", "City", "Country", "12345");
        ClientResponseDto response = new ClientResponseDto(1L, "John", "john@example.com", "Company", "Address", "City", "Country", "12345");
        when(clientService.updateClient(eq(1L), any(ClientRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DisplayName("Should delete client")
    void deleteClient_shouldSucceed() throws Exception {
        doNothing().when(clientService).deleteClient(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());
    }
}

