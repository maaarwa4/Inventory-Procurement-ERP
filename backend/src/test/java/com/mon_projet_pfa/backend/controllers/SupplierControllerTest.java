package com.mon_projet_pfa.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.services.SupplierService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSupplier() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setFirstName("Ali");
        supplier.setId(1L);

        Mockito.when(supplierService.create(any(Supplier.class))).thenReturn(supplier);

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ali"));
    }

    @Test
    void testGetAllSuppliers() throws Exception {
        Supplier s = new Supplier();
        s.setFirstName("Ali");
        List<Supplier> list = List.of(s);

        Mockito.when(supplierService.getAll(any(), any(), any(), any())).thenReturn(list);

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ali"));
    }

    @Test
    void testGetSupplierById() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setFirstName("Ali");

        Mockito.when(supplierService.getById(1L)).thenReturn(supplier);

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ali"));
    }
}
