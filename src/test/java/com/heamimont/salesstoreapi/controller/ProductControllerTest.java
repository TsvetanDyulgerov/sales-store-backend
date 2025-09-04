package com.heamimont.salesstoreapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Clean DB before each test
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Laptop");
        product.setDescription("High-end gaming laptop");
        product.setActualPrice(BigDecimal.valueOf(1200));
        product.setSellingPrice(BigDecimal.valueOf(1500));
        product.setAvailableQuantity(10);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.sellingPrice").value(1500));

        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllProducts_ShouldReturnList() throws Exception {
        // Seed one product
        Product product = new Product();
        product.setName("Phone");
        product.setDescription("Smartphone");
        product.setActualPrice(BigDecimal.valueOf(500));
        product.setSellingPrice(BigDecimal.valueOf(700));
        product.setAvailableQuantity(20);
        productRepository.save(product);

        mockMvc.perform(get("/api/products/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Phone"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductById_ShouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setName("Tablet");
        product.setDescription("Android tablet");
        product.setActualPrice(BigDecimal.valueOf(300));
        product.setSellingPrice(BigDecimal.valueOf(400));
        product.setAvailableQuantity(5);
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/api/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tablet"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Product product = new Product();
        product.setName("Old Name");
        product.setDescription("Old description");
        product.setActualPrice(BigDecimal.valueOf(100));
        product.setSellingPrice(BigDecimal.valueOf(200));
        product.setAvailableQuantity(5);
        Product saved = productRepository.save(product);

        UpdateProductDTO updated = new UpdateProductDTO();
        updated.setName("Updated Name");
        updated.setDescription("Updated description");
        updated.setSellingPrice(BigDecimal.valueOf(250));

        mockMvc.perform(put("/api/products/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.sellingPrice").value(250));

        assertThat(productRepository.findById(saved.getId()))
                .get()
                .extracting(Product::getName)
                .isEqualTo("Updated Name");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_ShouldRemoveProduct() throws Exception {
        Product product = new Product();
        product.setName("Delete Me");
        product.setDescription("To be deleted");
        product.setActualPrice(BigDecimal.valueOf(50));
        product.setSellingPrice(BigDecimal.valueOf(75));
        product.setAvailableQuantity(2);
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/api/products/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }
}
