package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.services.SupplierService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Valid
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        try {
            supplier.setId(null); // ⚡ assure que JPA génère l'ID
            Supplier createdSupplier = supplierService.create(supplier);
            return ResponseEntity.ok(createdSupplier);
        } catch (Exception e) {
            e.printStackTrace(); // affiche l’erreur complète dans la console backend
            return ResponseEntity.status(500).body("Error creating supplier: " + e.getMessage());
        }
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            supplierService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // ✅ Activer/Désactiver
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateSupplier(@PathVariable Long id) {
        try {
            supplierService.activateSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateSupplier(@PathVariable Long id) {
        try {
            supplierService.deactivateSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}