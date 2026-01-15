package com.inventory.imsbackend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.inventory.imsbackend.product.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByNameContainingIgnoreCase(String name);

    // paginated version but keeping old findBystatus for now
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // search by name (partial match) AND category , with Pagination
    Page<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndStatus(
            String name, String category, ProductStatus status, Pageable pageable );

    // if only want to search by Name or Category specifically
    Page<Product> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status, Pageable pageable );
}
