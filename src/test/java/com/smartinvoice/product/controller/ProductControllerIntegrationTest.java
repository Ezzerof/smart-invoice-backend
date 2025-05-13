package com.smartinvoice.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinvoice.product.dto.ProductRequestDto;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("Logo Design", "Clean and modern logo", 150.0, 1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Logo Design"));
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        productRepository.save(Product.builder().name("Flyer Design").description("Event flyer").price(75.0).build());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        Product product = productRepository.save(Product.builder().name("Poster Design").description("A2 Poster").price(90.0).build());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Poster Design"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product existing = productRepository.save(Product.builder().name("Old").description("Old desc").price(20.0).build());

        ProductRequestDto updated = new ProductRequestDto("Updated", "New desc", 45.0, 1);

        mockMvc.perform(put("/api/products/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.price").value(45.0));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Product product = productRepository.save(Product.builder().name("To delete").description("delete me").price(10.0).build());

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());
    }
}
