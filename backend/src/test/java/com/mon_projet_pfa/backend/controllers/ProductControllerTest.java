package com.mon_projet_pfa.backend.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.services.EnumService;
import com.mon_projet_pfa.backend.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private EnumService enumService;

    private ObjectMapper objectMapper;
    private List<Product> expectedProducts;
    private String expectedJson;

    @BeforeEach
    void setup() throws Exception {
        // Configure ObjectMapper pour gérer LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Charge les données de test
        Path jsonPath = new ClassPathResource("products_expected.json").getFile().toPath();
        expectedJson = Files.readString(jsonPath);
        expectedProducts = objectMapper.readValue(expectedJson, new TypeReference<>() {
        });
    }

    @Test
    void testGetAllProducts() throws Exception {
        Mockito.when(productService.getAll()).thenReturn(expectedProducts);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testGetProductById() throws Exception {
        Product p1 = expectedProducts.get(0);
        Mockito.when(productService.getById(1L)).thenReturn(p1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(p1)));
    }

    @Test
    void testCreateProduct() throws Exception {
        Product p1 = expectedProducts.get(0);
        Mockito.when(productService.create(Mockito.any(Product.class))).thenReturn(p1);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(p1)));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updated = expectedProducts.get(0);
        updated.setName("iPhone 15 Pro");
        Mockito.when(productService.update(Mockito.eq(1L), Mockito.any(Product.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Configure le mock pour ne rien faire lors de la suppression
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService, Mockito.times(1)).deleteProduct(1L);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // Modifié pour simuler une exception spécifique
        Mockito.when(productService.getById(999L))
                .thenThrow(new RuntimeException("Product not found with id: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        Product updated = expectedProducts.get(0);
        Mockito.when(productService.update(Mockito.eq(999L), Mockito.any(Product.class)))
                .thenThrow(new RuntimeException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}