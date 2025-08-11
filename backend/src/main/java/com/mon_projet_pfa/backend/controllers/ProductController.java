package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.dtos.PageResponse;
import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.services.EnumService;
import com.mon_projet_pfa.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private EnumService enumService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        try {
            List<Product> products = productService.getAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable int id) {
        try {
            Product product = productService.getById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        try {
            Product createdProduct = productService.create(product);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable int id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.update(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dropdown-data")
    public ResponseEntity<DropdownData> getDropdownData() {
        try {
            DropdownData data = new DropdownData();
            data.brands = enumService.getEnumValues("brand");
            data.categories = enumService.getEnumValues("category");
            data.colors = enumService.getEnumValues("color");
            data.storageOptions = enumService.getEnumValues("storage_gb");
            data.screenSizes = List.of(5.0, 5.5, 6.1, 6.7);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/search")
    public PageResponse<Product> searchProducts(
            @RequestBody ProductSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return productService.searchProducts(criteria, PageRequest.of(page, size));
    }

    public static class DropdownData {
        public List<String> brands;
        public List<String> categories;
        public List<String> colors;
        public List<String> storageOptions;
        public List<Double> screenSizes;
    }
}