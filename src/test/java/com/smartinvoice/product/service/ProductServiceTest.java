package com.smartinvoice.product.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.product.dto.ProductFilterRequest;
import com.smartinvoice.product.dto.ProductRequestDto;
import com.smartinvoice.product.dto.ProductResponseDto;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create service product and log it")
    void shouldCreateProduct() {
        ProductRequestDto request = new ProductRequestDto("Create a logo", "Design a unique logo for branding", 150.00, 1);
        Product saved = Product.builder().id(1L).name("Create a logo").description("Design a unique logo for branding").price(150.00).quantity(1).build();

        when(repository.save(any(Product.class))).thenReturn(saved);

        ProductResponseDto result = productService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Create a logo");
        verify(auditLogService).log("CREATE", "Product", "1");
    }

    @Test
    @DisplayName("Should get all service products")
    void shouldGetAllProducts() {
        Product p = Product.builder().id(1L).name("Animate a logo").description("Provide animated logo for videos").price(200.00).quantity(1).build();
        when(repository.findAll()).thenReturn(List.of(p));

        List<ProductResponseDto> products = productService.getAll();

        assertThat(products).hasSize(1);
        assertThat(products.get(0).name()).isEqualTo("Animate a logo");
    }

    @Test
    @DisplayName("Should return service product by ID")
    void shouldGetById() {
        Product p = Product.builder().id(1L).name("Design webpage").description("Design landing page for startup").price(300.0).quantity(1).build();
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        ProductResponseDto result = productService.getById(1L);

        assertThat(result.name()).isEqualTo("Design webpage");
    }

    @Test
    @DisplayName("Should throw if service product by ID not found")
    void shouldThrowWhenNotFoundById() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    @DisplayName("Should throw when updating non-existent product")
    void shouldThrowWhenUpdatingNonExistentProduct() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ProductRequestDto dto = new ProductRequestDto("Edit", "Updated", 100.0, 1);

        assertThatThrownBy(() -> productService.update(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    @DisplayName("Should throw when deleting non-existent product")
    void shouldThrowWhenDeletingNonExistentProduct() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    @DisplayName("Should update existing service product")
    void shouldUpdateProduct() {
        Product existing = Product.builder().id(1L).name("Design brochure").description("Marketing brochure").price(100.0).quantity(1).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenReturn(existing);

        ProductRequestDto dto = new ProductRequestDto("Design updated brochure", "Updated brochure design", 120.00, 1);

        ProductResponseDto updated = productService.update(1L, dto);

        assertThat(updated.name()).isEqualTo("Design updated brochure");
        verify(auditLogService).log("UPDATE", "Product", "1");
    }

    @Test
    @DisplayName("Should delete service product")
    void shouldDeleteProduct() {
        Product p = Product.builder().id(1L).name("Design business card").build();
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        productService.delete(1L);

        verify(repository).delete(p);
        verify(auditLogService).log("DELETE", "Product", "1");
    }

    @Test
    @DisplayName("Should filter service products by keyword")
    void shouldFilterProducts() {
        ProductFilterRequest filter = new ProductFilterRequest("logo", "name");
        Product match = Product.builder().id(1L).name("Logo animation").description("Animated logo for branding").price(180.0).quantity(1).build();
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(match));

        List<ProductResponseDto> result = productService.getFilteredProducts(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).containsIgnoringCase("logo");
    }

    @Test
    @DisplayName("Should return empty list when no products match filter")
    void shouldReturnEmptyListForNoFilterMatch() {
        ProductFilterRequest filter = new ProductFilterRequest("nonexistent", "name");
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(Collections.emptyList());

        List<ProductResponseDto> result = productService.getFilteredProducts(filter);

        assertThat(result).isEmpty();
    }
}
