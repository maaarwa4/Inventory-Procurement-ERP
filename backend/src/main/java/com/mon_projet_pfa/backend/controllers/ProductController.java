package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.dtos.PageResponse;
import com.mon_projet_pfa.backend.dtos.ProductCreateDTO;
import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.StorageCapacity;
import com.mon_projet_pfa.backend.enums.Color;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.services.EnumService;
import com.mon_projet_pfa.backend.enums.Category;
import com.mon_projet_pfa.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mon_projet_pfa.backend.models.Supplier;
import java.util.List;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierRepository supplierRepository;

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
    public ResponseEntity<Product> getById(@PathVariable Long id) {
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
    public Product create(@RequestBody ProductCreateDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());

        // Conversion String -> Enum
        product.setBrand(Brand.valueOf(dto.getBrand().toUpperCase()));
        product.setCategory(Category.valueOf(dto.getCategory().toUpperCase()));
        product.setColor(Color.valueOf(dto.getColor().toUpperCase()));
        product.setStorage(StorageCapacity.valueOf(dto.getStorage().toUpperCase()));

        product.setPrice(dto.getPrice());
        product.setModel(dto.getModel());
        product.setStockQuantity(dto.getStock_quantity());
        product.setImageUrl(dto.getImage_url());
        product.setScreenSize(dto.getScreen_size());
        product.setNetworkType(dto.getNetwork_type());

        // Récupération du Supplier depuis son ID
        Supplier supplier = supplierRepository.findById(dto.getSupplier_id())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        product.setSupplier(supplier);

        return productService.create(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductCreateDTO dto) {
        try {
            // First, get the existing product
            Product existingProduct = productService.getById(id);

            // Update the fields from DTO
            existingProduct.setName(dto.getName());
            existingProduct.setDescription(dto.getDescription());

            // Conversion String -> Enum (with null checks)
            if (dto.getBrand() != null && !dto.getBrand().isEmpty()) {
                existingProduct.setBrand(Brand.valueOf(dto.getBrand().toUpperCase()));
            }
            if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
                existingProduct.setCategory(Category.valueOf(dto.getCategory().toUpperCase()));
            }
            if (dto.getColor() != null && !dto.getColor().isEmpty()) {
                existingProduct.setColor(Color.valueOf(dto.getColor().toUpperCase()));
            }
            if (dto.getStorage() != null && !dto.getStorage().isEmpty()) {
                existingProduct.setStorage(StorageCapacity.valueOf(dto.getStorage().toUpperCase()));
            }

            existingProduct.setPrice(dto.getPrice());
            existingProduct.setModel(dto.getModel());
            existingProduct.setStockQuantity(dto.getStock_quantity());
            existingProduct.setImageUrl(dto.getImage_url());
            existingProduct.setScreenSize(dto.getScreen_size());
            existingProduct.setNetworkType(dto.getNetwork_type());

            // Update supplier if provided
            if (dto.getSupplier_id() != null) {
                Supplier supplier = supplierRepository.findById(dto.getSupplier_id())
                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
                existingProduct.setSupplier(supplier);
            }

            Product updatedProduct = productService.update(id, existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
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

            // Retourner les valeurs des enums directement
            data.brands = Arrays.stream(Brand.values())
                    .map(Brand::name)
                    .collect(Collectors.toList());

            data.categories = Arrays.stream(Category.values())
                    .map(Category::name)
                    .collect(Collectors.toList());

            data.storageOptions = Arrays.stream(StorageCapacity.values())
                    .map(StorageCapacity::name)
                    .collect(Collectors.toList());

            data.colors = Arrays.stream(Color.values())
                    .map(Color::name)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public static class DropdownData {
        public List<String> brands;
        public List<String> categories;
        public List<String> colors;
        public List<String> storageOptions;
    }

    // CORRECTION MAJEURE: Utiliser @PostMapping au lieu de @GetMapping avec des
    // paramètres
    @PostMapping("/search")
    public ResponseEntity<PageResponse<Product>> searchProducts(
            @RequestBody SearchRequest request) {

        try {
            ProductSearchCriteria criteria = request.getCriteria();
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 12;

            // Conversion et validation des enums
            String brandValue = null;
            String categoryValue = null;
            String storageValue = null;

            if (criteria.hasBrandFilter()) {
                try {
                    Brand.valueOf(criteria.getBrand().toUpperCase());
                    brandValue = criteria.getBrand().toUpperCase();
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid brand value: " + criteria.getBrand());
                }
            }

            if (criteria.hasCategoryFilter()) {
                try {
                    Category.valueOf(criteria.getCategory().toUpperCase());
                    categoryValue = criteria.getCategory().toUpperCase();
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid category value: " + criteria.getCategory());
                }
            }

            if (criteria.hasStorageFilter()) {
                try {
                    StorageCapacity.valueOf(criteria.getStorage().toUpperCase());
                    storageValue = criteria.getStorage().toUpperCase();
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid storage value: " + criteria.getStorage());
                }
            }

            // Créer les critères convertis
            ProductSearchCriteria convertedCriteria = new ProductSearchCriteria(
                    criteria.getName(),
                    brandValue,
                    categoryValue,
                    criteria.getPriceMin(),
                    criteria.getPriceMax(),
                    storageValue,
                    criteria.getModel());

            PageResponse<Product> result = productService.searchProducts(
                    convertedCriteria, PageRequest.of(page, size));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Classe pour encapsuler la requête de recherche
    public static class SearchRequest {
        private ProductSearchCriteria criteria;
        private Integer page;
        private Integer size;

        public SearchRequest() {
        }

        public ProductSearchCriteria getCriteria() {
            return criteria;
        }

        public void setCriteria(ProductSearchCriteria criteria) {
            this.criteria = criteria;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }

    // GET products by supplier ID
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplier(@PathVariable Long supplierId) {
        try {
            List<Product> products = productService.getProductsBySupplierId(supplierId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}