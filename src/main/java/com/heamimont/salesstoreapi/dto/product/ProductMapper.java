package com.heamimont.salesstoreapi.dto.product;

import com.heamimont.salesstoreapi.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setActualPrice(dto.getActualPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setAvailableQuantity(dto.getAvailableQuantity());
        return product;
    }

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
}
