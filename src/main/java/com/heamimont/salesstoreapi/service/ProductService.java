package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing products in the sales store API.
 * Provides methods to retrieve, create, and update products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public void updateProduct(Long id, Product product) {
        Product targetProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getName() != null) {
            targetProduct.setName(product.getName());
        }

        if (product.getActualPrice() != null) {
            targetProduct.setActualPrice(product.getActualPrice());
        }

        if (product.getSellingPrice() != null) {
            targetProduct.setSellingPrice(product.getSellingPrice());
        }

        if (product.getDescription() != null) {
            targetProduct.setDescription(product.getDescription());
        }

        productRepository.save(targetProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
