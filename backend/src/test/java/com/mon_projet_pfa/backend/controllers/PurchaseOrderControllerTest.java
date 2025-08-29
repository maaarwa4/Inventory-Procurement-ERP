package com.mon_projet_pfa.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.repositories.PurchaseOrderRepository;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
import com.mon_projet_pfa.backend.services.PurchaseOrderPdfService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseOrderController.class)
class PurchaseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;
    @MockBean
    private SupplierRepository supplierRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private PurchaseOrderPdfService purchaseOrderPdfService;

    @Test
    void testGetOrderById() throws Exception {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(1L);
        order.setCreationDate(LocalDate.now());
        

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/purchase-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllOrders() throws Exception {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(1L);
       

        when(purchaseOrderRepository.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/purchase-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
