package com.smartinvoice.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinvoice.product.dto.ProductFilterRequest;
import com.smartinvoice.product.dto.ProductRequestDto;
import com.smartinvoice.product.dto.ProductResponseDto;
import com.smartinvoice.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Autowired
    private ProductService productService;

    @Test
    @WithMockUser
    @DisplayName("Should create product")
    void shouldCreateProduct() throws Exception {
        ProductRequestDto request = new ProductRequestDto("Logo Design", "Professional logo", 100.0, 1);
        ProductResponseDto response = new ProductResponseDto(1L, "Logo Design", "Professional logo", 100.0, 1);

        Mockito.when(productService.create(any(ProductRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all products")
    void shouldGetAllProducts() throws Exception {
        ProductResponseDto product = new ProductResponseDto(1L, "Logo Design", "Professional logo", 100.0, 1);
        Mockito.when(productService.getAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Logo Design"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get product by ID")
    void shouldGetById() throws Exception {
        ProductResponseDto product = new ProductResponseDto(1L, "Logo Design", "Professional logo", 100.0, 1);
        Mockito.when(productService.getById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update product")
    void shouldUpdateProduct() throws Exception {
        ProductRequestDto update = new ProductRequestDto("Logo Update", "Updated design", 120.0, 1);
        ProductResponseDto updated = new ProductResponseDto(1L, "Logo Update", "Updated design", 120.0, 1);

        Mockito.when(productService.update(eq(1L), any(ProductRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Logo Update"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete product")
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).delete(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should filter products")
    void shouldFilterProducts() throws Exception {
        ProductResponseDto product = new ProductResponseDto(1L, "Logo Design", "Animated", 150.0, 1);
        Mockito.when(productService.getFilteredProducts(any(ProductFilterRequest.class))).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/filter")
                        .param("keyword", "logo")
                        .param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Logo Design"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 on invalid create request")
    void shouldFailValidationOnCreate() throws Exception {
        ProductRequestDto invalid = new ProductRequestDto("", "", -10.0, -1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 on invalid update request")
    void shouldFailValidationOnUpdate() throws Exception {
        ProductRequestDto invalid = new ProductRequestDto("", null, -20.0, -1);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle empty filter result")
    void shouldHandleEmptyFilterResult() throws Exception {
        Mockito.when(productService.getFilteredProducts(any(ProductFilterRequest.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/products/filter")
                        .param("keyword", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
