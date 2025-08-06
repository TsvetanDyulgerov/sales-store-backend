package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByAvailableQuantityLessThanEqual(int threshold);
    List<Product> findBySellingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByAvailableQuantityGreaterThan(int quantity);
    boolean existsByName(String name);
}
