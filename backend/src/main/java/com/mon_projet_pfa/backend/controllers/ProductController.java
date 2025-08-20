package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.dtos.PageResponse;
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
public ResponseEntity<Product> create(@RequestBody Map<String, Object> body) {
    try {
        Integer supplierId = (Integer) body.get("supplierId");
        Supplier supplier = new Supplier();
        supplier.setId(Long.valueOf(supplierId));

        Product product = new Product();
        product.setName((String) body.get("name"));
        product.setBrand(Brand.valueOf((String) body.get("brand")));
        product.setCategory(Category.valueOf((String) body.get("category")));
        product.setPrice(new BigDecimal(body.get("price").toString()));
        product.setStockQuantity((Integer) body.get("stock_quantity"));
        product.setSupplier(supplier);
        // ... map other fields ...

        Product created = productService.create(product);
        return ResponseEntity.ok(created);
    } catch (Exception e) {
        e.printStackTrace();
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