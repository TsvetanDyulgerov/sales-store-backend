package com.heamimont.salesstoreapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductTests {
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        product.setId(id);
        assertEquals(id, product.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        String name = "Test Product";
        product.setName(name);
        assertEquals(name, product.getName());
    }

    @Test
    void testDescriptionGetterAndSetter() {
        String description = "Test product description";
        product.setDescription(description);
        assertEquals(description, product.getDescription());
    }

    @Test
    void testActualPriceGetterAndSetter() {
        BigDecimal actualPrice = new BigDecimal("99.99");
        product.setActualPrice(actualPrice);
        assertEquals(actualPrice, product.getActualPrice());
    }

    @Test
    void testSellingPriceGetterAndSetter() {
        BigDecimal sellingPrice = new BigDecimal("149.99");
        product.setSellingPrice(sellingPrice);
        assertEquals(sellingPrice, product.getSellingPrice());
    }

    @Test
    void testAvailableQuantityGetterAndSetter() {
        int quantity = 10;
        product.setAvailableQuantity(quantity);
        assertEquals(quantity, product.getAvailableQuantity());
    }

    @Test
    void testInitialValues() {
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getActualPrice());
        assertNull(product.getSellingPrice());
        assertEquals(0, product.getAvailableQuantity());
    }

    @Test
    void testCompleteProductSetup() {
        // Arrange
        Long id = 1L;
        String name = "Complete Product";
        String description = "Complete product description";
        BigDecimal actualPrice = new BigDecimal("100.00");
        BigDecimal sellingPrice = new BigDecimal("150.00");
        int availableQuantity = 5;

        // Act
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setActualPrice(actualPrice);
        product.setSellingPrice(sellingPrice);
        product.setAvailableQuantity(availableQuantity);

        // Assert
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(actualPrice, product.getActualPrice());
        assertEquals(sellingPrice, product.getSellingPrice());
        assertEquals(availableQuantity, product.getAvailableQuantity());
    }
}
