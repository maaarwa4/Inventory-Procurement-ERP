package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    // CREATE
    @Transactional
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // READ ALL WITH FILTERS
    public List<Supplier> getAll(String search, String city, String country, Boolean active) {
        if (search != null && city != null) {
            return supplierRepository.findByFirstNameContainingIgnoreCaseAndCity(search, city);
        } else if (search != null) {
            return supplierRepository.findByFirstNameContainingIgnoreCase(search);
        } else if (city != null) {
            return supplierRepository.findByCity(city);
        } else if (country != null) {
            return supplierRepository.findByCountry(country);
        } else if (active != null) {
            return supplierRepository.findByIsActive(active);
        }
        return supplierRepository.findAll();
    }

    // SEARCH METHOD (for frontend)
    public List<Supplier> search(String keyword, String city) {
        if (keyword != null && city != null) {
            return supplierRepository.findByFirstNameContainingIgnoreCaseAndCity(keyword, city);
        } else if (keyword != null) {
            return supplierRepository.searchSuppliers(keyword);
        } else if (city != null) {
            return supplierRepository.findByCity(city);
        }
        return supplierRepository.findAll();
    }

    // READ BY ID
    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    // UPDATE
    @Transactional
    public Supplier update(Long id, Supplier updatedSupplier) {
        Supplier existing = getById(id);
        updatedSupplier.setId(existing.getId());
        return supplierRepository.save(updatedSupplier);
    }

    // DEACTIVATE (Soft Delete)
    @Transactional
    public void disable(Long id) {
        supplierRepository.deactivateById(id);
    }
}