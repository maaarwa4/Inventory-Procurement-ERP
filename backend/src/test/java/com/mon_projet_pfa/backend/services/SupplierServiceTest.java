package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setFirstName("Ali");
        supplier.setCity("Casablanca");
        supplier.setCountry("Maroc");
        supplier.setIsActive(true);
    }

    @Test
    void testCreateSupplier() {
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        Supplier created = supplierService.create(supplier);
        assertNotNull(created);
        assertEquals("Ali", created.getFirstName());
    }

    @Test
    void testGetSupplierById() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Supplier found = supplierService.getById(1L);
        assertEquals("Ali", found.getFirstName());
    }

    @Test
    void testDisableSupplier() {
        doNothing().when(supplierRepository).deactivateById(1L);
        assertDoesNotThrow(() -> supplierService.disable(1L));
        verify(supplierRepository, times(1)).deactivateById(1L);
    }

    @Test
    void testUpdateSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(i -> i.getArguments()[0]);

        Supplier updated = new Supplier();
        updated.setFirstName("Youssef");

        Supplier result = supplierService.update(1L, updated);

        assertEquals("Youssef", result.getFirstName());
        assertEquals(1L, result.getId());
    }
}
