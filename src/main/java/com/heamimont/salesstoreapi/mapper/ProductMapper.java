package com.heamimont.salesstoreapi.mapper;

import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.dto.product.ProductPublicResponseDTO;
import com.heamimont.salesstoreapi.dto.product.ProductResponseDTO;
import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
import com.heamimont.salesstoreapi.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // Method to convert CreateProductDTO to Product entity
    public Product toEntity(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setActualPrice(dto.getActualPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setAvailableQuantity(dto.getAvailableQuantity());
        return product;
    }

    // Method to update Product entity from UpdateProductDTO
    public void updateEntity(Product product, UpdateProductDTO dto) {
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getActualPrice() != null) {
            product.setActualPrice(dto.getActualPrice());
        }
        if (dto.getSellingPrice() != null) {
            product.setSellingPrice(dto.getSellingPrice());
        }
        if (dto.getAvailableQuantity() != null) {
            product.setAvailableQuantity(dto.getAvailableQuantity());
        }
    }

    // Method to convert Product entity to ProductResponseDTO
    public ProductResponseDTO toDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setActualPrice(product.getActualPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setAvailableQuantity(product.getAvailableQuantity());
        return dto;
    }

    // Method to convert Product entity to ProductPublicResponseDTO
    public ProductPublicResponseDTO toPublicDTO(Product product) {
        ProductPublicResponseDTO dto = new ProductPublicResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setAvailableQuantity(product.getAvailableQuantity());
        return dto;
    }
}
