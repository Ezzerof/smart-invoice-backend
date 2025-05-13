package com.smartinvoice.client.repository;

import com.smartinvoice.client.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
