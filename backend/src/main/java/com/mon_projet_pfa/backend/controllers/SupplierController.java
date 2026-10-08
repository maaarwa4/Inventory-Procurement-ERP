package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.services.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Valid
public class SupplierController {
    private final SupplierService supplierService;

    
    @PostMapping
    public ResponseEntity<Supplier> create(@RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.create(supplier);
        return ResponseEntity.ok(createdSupplier);
    }

    // READ ALL (with optional filters)
    @GetMapping
    public ResponseEntity<List<Supplier>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Boolean active) {

        return ResponseEntity.ok(supplierService.getAll(search, city, country, active));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(
            @PathVariable Long id,
            @RequestBody Supplier supplierDetails) {
        Supplier updatedSupplier = supplierService.update(id, supplierDetails);
        return ResponseEntity.ok(updatedSupplier);
    }

    // DEACTIVATE (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        supplierService.disable(id);
        return ResponseEntity.noContent().build();
    }

    // SEARCH ENDPOINT (Alternative)
    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(supplierService.search(keyword, city));
    }
}