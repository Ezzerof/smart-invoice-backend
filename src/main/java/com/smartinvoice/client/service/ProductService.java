package com.smartinvoice.client.service;

import com.smartinvoice.client.dto.ProductRequestDto;
import com.smartinvoice.client.dto.ProductResponseDto;
import com.smartinvoice.client.entity.Product;
import com.smartinvoice.client.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponseDto create(ProductRequestDto dto) {
        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();

        Product saved = repository.save(product);
        return mapToDto(saved);
    }

    public List<ProductResponseDto> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return mapToDto(product);
    }

    public ProductResponseDto update(Long id, ProductRequestDto dto) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setQuantity(dto.quantity());

        return mapToDto(repository.save(existing));
    }

    public void delete(Long id) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        repository.delete(existing);
    }

    private ProductResponseDto mapToDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
