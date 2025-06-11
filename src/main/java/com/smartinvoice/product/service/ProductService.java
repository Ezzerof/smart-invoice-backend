package com.smartinvoice.product.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.product.dto.ProductFilterRequest;
import com.smartinvoice.product.dto.ProductRequestDto;
import com.smartinvoice.product.dto.ProductResponseDto;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final AuditLogService auditLogService;

    public ProductResponseDto create(ProductRequestDto dto) {
        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();

        Product saved = repository.save(product);

        auditLogService.log("CREATE", "Product", String.valueOf(saved.getId()));

        return mapToDto(saved);
    }

    public List<ProductResponseDto> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToDto(product);
    }

    public ProductResponseDto update(Long id, ProductRequestDto dto) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setQuantity(dto.quantity());

        Product updated = repository.save(existing);

        auditLogService.log("UPDATE", "Product", String.valueOf(updated.getId()));

        return mapToDto(updated);
    }

    public void delete(Long id) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        repository.delete(existing);

        auditLogService.log("DELETE", "Product", String.valueOf(existing.getId()));
    }

    public List<ProductResponseDto> getFilteredProducts(ProductFilterRequest filter) {
        return repository.findAll(withFilters(filter), getSort(filter.sortBy()))
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Specification<Product> withFilters(ProductFilterRequest filter) {
        return (root, query, cb) -> {
            if (filter.keyword() != null && !filter.keyword().isBlank()) {
                String kw = "%" + filter.keyword().toLowerCase() + "%";
                return cb.like(cb.lower(root.get("name")), kw);
            }
            return cb.conjunction();
        };
    }

    private Sort getSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) return Sort.unsorted();

        return switch (sortBy) {
            case "name" -> Sort.by(Sort.Direction.ASC, "name");
            case "-name" -> Sort.by(Sort.Direction.DESC, "name");
            case "price" -> Sort.by(Sort.Direction.ASC, "price");
            case "-price" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.unsorted();
        };
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
