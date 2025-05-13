package com.smartinvoice.product.service;

import com.smartinvoice.product.dto.ProductRequestDto;
import com.smartinvoice.product.dto.ProductResponseDto;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void testCreateProduct() {
        ProductRequestDto dto = new ProductRequestDto("Logo Design", "Professional logo creation", 120.00, 1);
        Product savedProduct = Product.builder()
                .id(1L)
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .build();

        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDto result = service.create(dto);

        assertEquals("Logo Design", result.name());
        assertEquals("Professional logo creation", result.description());
        assertEquals(120.00, result.price());
    }

    @Test
    void testGetAllProducts() {
        List<Product> mockProducts = Arrays.asList(
                Product.builder().id(1L).name("Logo Design").description("Professional logo").price(120.00).build(),
                Product.builder().id(2L).name("Business Card Design").description("Front & back card").price(60.00).build()
        );

        when(repository.findAll()).thenReturn(mockProducts);

        List<ProductResponseDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Logo Design", result.get(0).name());
        assertEquals("Business Card Design", result.get(1).name());
    }

    @Test
    void testGetProductById_Existing() {
        Product product = Product.builder()
                .id(1L)
                .name("Website Mockup")
                .description("Responsive web layout")
                .price(300.00)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto result = service.getById(1L);

        assertEquals("Website Mockup", result.name());
    }

    @Test
    void testGetProductById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void testUpdateProduct() {
        Product existing = Product.builder()
                .id(1L)
                .name("Old Name")
                .description("Old Description")
                .price(100.00)
                .build();

        ProductRequestDto dto = new ProductRequestDto("Brand Identity Package", "Full branding set", 450.00, 1);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto result = service.update(1L, dto);

        assertEquals("Brand Identity Package", result.name());
        assertEquals("Full branding set", result.description());
        assertEquals(450.00, result.price());
    }

    @Test
    void testDeleteProduct() {
        Product product = Product.builder().id(1L).name("Temp").description("To delete").price(10.0).build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository, times(1)).delete(product);
    }
}
