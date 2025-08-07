package com.heamimont.salesstoreapi.dto.product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract class ProductDTO {
    @NotBlank(message = "Product name is required")
    @Size(max = 50, message = "Product name must not exceed 50 characters")
    protected String name;

    @Size(max = 400, message = "Description must not exceed 400 characters")
    protected String description;
}
