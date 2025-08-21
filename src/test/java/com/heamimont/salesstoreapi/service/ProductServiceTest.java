package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.product.*;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.mapper.ProductMapper;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponseDTO productResponseDTO;
    private CreateProductDTO createProductDTO;
    private UpdateProductDTO updateProductDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product(1L, "Product A", "Description A",
                new BigDecimal("10.00"), new BigDecimal("15.00"), 100);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Product A");
        productResponseDTO.setDescription("Description A");
        productResponseDTO.setActualPrice(new BigDecimal("10.00"));
        productResponseDTO.setSellingPrice(new BigDecimal("15.00"));
        productResponseDTO.setAvailableQuantity(100);

        createProductDTO = new CreateProductDTO();
        createProductDTO.setName("Product A");
        createProductDTO.setDescription("Description A");
        createProductDTO.setActualPrice(new BigDecimal("10.00"));
        createProductDTO.setSellingPrice(new BigDecimal("15.00"));
        createProductDTO.setAvailableQuantity(100);

        updateProductDTO = new UpdateProductDTO();
        updateProductDTO.setName("Updated Product A");
        updateProductDTO.setDescription("Updated Description A");
        updateProductDTO.setActualPrice(new BigDecimal("12.00"));
        updateProductDTO.setSellingPrice(new BigDecimal("18.00"));
        updateProductDTO.setAvailableQuantity(120);
    }

    @Test
    void testGetAllProducts_ReturnsListOfDTOs() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        List<ProductResponseDTO> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Product A", products.get(0).getName());

        verify(productRepository).findAll();
        verify(productMapper).toDTO(product);
    }

    @Test
    void testGetAllProducts_ThrowsResourceNotFoundException_OnError() {
        when(productRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(ResourceNotFoundException.class, () -> productService.getAllProducts());
    }

    @Test
    void testGetProductById_ReturnsProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository).findById(1L);
        verify(productMapper).toDTO(product);
    }

    @Test
    void testGetProductById_ThrowsResourceNotFoundException_WhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testCreateProduct_ReturnsCreatedProductDTO() {
        when(productMapper.toEntity(createProductDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(createProductDTO);

        assertNotNull(result);
        assertEquals("Product A", result.getName());

        verify(productMapper).toEntity(createProductDTO);
        verify(productRepository).save(product);
        verify(productMapper).toDTO(product);
    }

    @Test
    void testCreateProduct_ThrowsResourceCreationException_OnError() {
        when(productMapper.toEntity(createProductDTO)).thenReturn(product);
        when(productRepository.save(product)).thenThrow(new RuntimeException("DB error"));

        assertThrows(ResourceCreationException.class, () -> productService.createProduct(createProductDTO));
    }

    @Test
    void testUpdateProduct_ReturnsUpdatedProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // updateEntity is void, so no return, just verify called
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            UpdateProductDTO dto = invocation.getArgument(1);

            // Simulate update
            p.setName(dto.getName());
            p.setDescription(dto.getDescription());
            p.setActualPrice(dto.getActualPrice());
            p.setSellingPrice(dto.getSellingPrice());
            p.setAvailableQuantity(dto.getAvailableQuantity());

            return null;
        }).when(productMapper).updateEntity(product, updateProductDTO);

        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, updateProductDTO);

        assertNotNull(result);
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntity(product, updateProductDTO);
        verify(productRepository).save(product);
        verify(productMapper).toDTO(product);
    }

    @Test
    void testUpdateProduct_ThrowsResourceNotFoundException_WhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateProductDTO));
    }

    @Test
    void testDeleteProduct_DeletesSuccessfully() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_ThrowsResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProduct_ThrowsResourceNotFoundException_OnDeleteError() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(productRepository).deleteById(1L);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
