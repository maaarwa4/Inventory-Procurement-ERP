package com.mon_projet_pfa.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private List<Product> expectedProducts;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        Path jsonPath = new ClassPathResource("products_expected.json").getFile().toPath();
        String json = Files.readString(jsonPath);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support des dates Java 8
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Lecture format texte

        expectedProducts = mapper.readValue(json, new TypeReference<>() {
        });
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> products = productService.getAll();

        assertEquals(expectedProducts.size(), products.size());
        assertEquals(expectedProducts.get(0).getName(), products.get(0).getName());
    }

    @Test
    void testGetProductById() {
        Product p1 = expectedProducts.get(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));

        Product product = productService.getById(1L);

        assertNotNull(product);
        assertEquals("iPhone 14 Pro", product.getName());
    }

    @Test
    void testCreateProduct() {
        Product p1 = expectedProducts.get(0);
        when(productRepository.save(p1)).thenReturn(p1);

        Product created = productService.create(p1);

        assertNotNull(created);
        assertEquals(p1.getName(), created.getName());
    }

    @Test
    void testUpdateProduct() {
        Product p1 = expectedProducts.get(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(p1)).thenReturn(p1);

        Product updated = productService.update(1L, p1);

        assertEquals(p1.getName(), updated.getName());
    }

   /* 
     @Test
    void testDeleteProduct() {
        Product p1 = expectedProducts.get(1);
    
        // Simuler qu'on trouve bien le produit avant suppression
        when(productRepository.findById(2)).thenReturn(Optional.of(p1));
    
        // Simuler que la suppression ne jette pas d'erreur
        doNothing().when(productRepository).deleteById(2);
    
        // Exécuter
        productService.deleteProduct(2);
    
        // Vérifier que deleteById a bien été appelé
        verify(productRepository, times(1)).deleteById(2);
    }
   */
    

}
