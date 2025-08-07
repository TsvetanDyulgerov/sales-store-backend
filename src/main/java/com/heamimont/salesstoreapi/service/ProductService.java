package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.*;
import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.dto.product.ProductMapper;
import com.heamimont.salesstoreapi.dto.product.ProductResponseDTO;
import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductResponseDTO> getAllProducts() {
        try {
            return productRepository.findAll().stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all products");
        }
    }

    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public ProductResponseDTO createProduct(CreateProductDTO createProductDTO) {
        try {
            Product product = productMapper.toEntity(createProductDTO);
            Product savedProduct = productRepository.save(product);
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create product: " + e.getMessage());
        }
    }

    public ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productMapper.updateEntity(product, updateProductDTO);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new ResourceNotFoundException("Product not found");
            }
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Product not found. Deletion failed");
        }
    }
}
