package com.mon_projet_pfa.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.repositories.PurchaseOrderRepository;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
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

class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private List<PurchaseOrder> expectedOrders;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        Path jsonPath = new ClassPathResource("purchase_orders_expected.json").getFile().toPath();
        String json = Files.readString(jsonPath);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        expectedOrders = mapper.readValue(json, new TypeReference<>() {
        });
    }

    @Test
    void testGetAllOrders() {
        when(purchaseOrderRepository.findAll()).thenReturn(expectedOrders);

        List<PurchaseOrder> orders = purchaseOrderService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals("PENDING", orders.get(0).getStatus().name());
    }

    @Test
    void testGetOrderById() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(expectedOrders.get(0)));

        Optional<PurchaseOrder> order = purchaseOrderService.getOrderById(1L);

        assertTrue(order.isPresent());
        
    }
}
