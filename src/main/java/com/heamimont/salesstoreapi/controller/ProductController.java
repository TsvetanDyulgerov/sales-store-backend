package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.dto.product.ProductPublicResponseDTO;
import com.heamimont.salesstoreapi.dto.product.ProductResponseDTO;
import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
import com.heamimont.salesstoreapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Retrieves a list of all products (admin view).
     */
    @Operation(summary = "Get All Products (Admin View)", description = "Retrieves a list of all products with detailed information. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of products"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /api/products/public
     * Retrieves a list of all products (public view).
     */
    @Operation(summary = "Get All Products (Public View)", description = "Public access of list of all products with limited information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of products"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping("/public")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductPublicResponseDTO>> getAllProductsPublic() {
        return ResponseEntity.ok(productService.getAllProductsPublic());
    }

    /**
     * GET /api/products/{id}
     * Retrieves a product by its ID.
     */
    @Operation(summary = "Get Product by ID", description = "Retrieves a product by its ID. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of the product"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * GET /api/products/name/{name}
     * Retrieves a product by its name.
     */
    @Operation(summary = "Get Product by Name", description = "Retrieves a product by its name. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of the product"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> getProductByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    /**
     * POST /api/products
     * Creates a new product.
     */
    @Operation(summary = "Create New Product", description = "Creates a new product. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(createProductDTO));
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product.
     */
    @Operation(summary = "Update Product", description = "Updates an existing product. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, updateProductDTO));
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product by its ID.
     */
    @Operation(summary = "Delete Product", description = "Deletes a product by its ID. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
