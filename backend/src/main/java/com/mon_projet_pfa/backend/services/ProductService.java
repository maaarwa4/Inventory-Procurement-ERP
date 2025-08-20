package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.dtos.PageResponse;
import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.Category;
import com.mon_projet_pfa.backend.enums.StorageCapacity;
import com.mon_projet_pfa.backend.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product update(int id, Product updatedProduct) {
        Product existing = getById(id);
        updatedProduct.setId(existing.getId());
        return productRepository.save(updatedProduct);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    public PageResponse<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        // Méthode alternative qui gère les enums correctement
        List<Product> allProducts = productRepository.findAll();
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> {
                    // Filtre par nom
                    if (criteria.hasNameFilter()
                            && !product.getName().toLowerCase().contains(criteria.getName().toLowerCase())) {
                        return false;
                    }

                    // Filtre par marque
                    if (criteria.hasBrandFilter()
                            && product.getBrand() != Brand.valueOf(criteria.getBrand().toUpperCase())) {
                        return false;
                    }

                    // Filtre par catégorie
                    if (criteria.hasCategoryFilter()
                            && product.getCategory() != Category.valueOf(criteria.getCategory().toUpperCase())) {
                        return false;
                    }

                    // Filtre par prix minimum
                    if (criteria.getPriceMin() != null && product.getPrice().doubleValue() < criteria.getPriceMin()) {
                        return false;
                    }

                    // Filtre par prix maximum
                    if (criteria.getPriceMax() != null && product.getPrice().doubleValue() > criteria.getPriceMax()) {
                        return false;
                    }

                    // Filtre par stockage
                    if (criteria.hasStorageFilter()
                            && product.getStorage() != StorageCapacity.valueOf(criteria.getStorage().toUpperCase())) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredProducts.size());

        List<Product> pageContent = start < filteredProducts.size() ? filteredProducts.subList(start, end) : List.of();

        Page<Product> page = new PageImpl<>(pageContent, pageable, filteredProducts.size());

        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
    }

    public List<Product> getProductsBySupplierId(Long id) {
        return productRepository.findBySupplier_Id(id);
    }

}
