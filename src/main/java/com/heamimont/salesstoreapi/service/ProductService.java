package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.product.ProductPublicResponseDTO;
import com.heamimont.salesstoreapi.mapper.ProductMapper;
import com.heamimont.salesstoreapi.dto.product.ProductResponseDTO;
import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * * Service class for managing products in the sales store API.
 * Provides methods to create, read, update, and delete products.
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return List of ProductResponseDTO containing all products
     * @throws ResourceNotFoundException if an error occurs while fetching products
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        try {
            return productRepository.findAll().stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all products");
        }
    }

    /**
     * Retrieves all products from the repository for public view.
     *
     * @return List of ProductPublicResponseDTO containing all products with limited details
     * @throws ResourceNotFoundException if an error occurs while fetching products
     */
    @Transactional(readOnly = true)
    public List<ProductPublicResponseDTO> getAllProductsPublic() {
        try {
            return productRepository.findAll().stream()
                    .map(productMapper::toPublicDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all products");
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return ProductResponseDTO containing the product details
     * @throws ResourceNotFoundException if the product with the given ID does not exist
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    /**
     * Retrieves a product by its name.
     *
     * @param name the name of the product to retrieve
     * @return ProductResponseDTO containing the product details
     * @throws ResourceNotFoundException if the product with the given name does not exist
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    /**
     * Creates a new product.
     *
     * @param createProductDTO the DTO containing product details
     * @return ProductResponseDTO containing the created product details
     * @throws ResourceCreationException if the product creation fails
     */
    @Transactional
    public ProductResponseDTO createProduct(CreateProductDTO createProductDTO) {
        try {
            Product product = productMapper.toEntity(createProductDTO);
            Product savedProduct = productRepository.save(product);
            logger.info("[Product Creation] Product ({}, {}) created successfully", savedProduct.getId(), savedProduct.getName());
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create product: " + e.getMessage());
        }
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id the ID of the product to update
     * @param updateProductDTO the DTO containing updated product details
     * @return ProductResponseDTO containing the updated product details
     * @throws ResourceNotFoundException if the product with the given ID does not exist
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productMapper.updateEntity(product, updateProductDTO);
        Product updatedProduct = productRepository.save(product);
        logger.info("[Product Update] Product ({}, {}) updated successfully", updatedProduct.getId(), updatedProduct.getName());
        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @throws ResourceNotFoundException if the product with the given ID does not exist
     */
    @Transactional
    public void deleteProduct(Long id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new ResourceNotFoundException("Product not found");
            }
            productRepository.deleteById(id);
            logger.info("[Product Deletion] Product ({}) deleted successfully", id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Product not found. Deletion failed");
        }
    }
}
