package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.dtos.PageResponse;
import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setBrand(updatedProduct.getBrand());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setModel(updatedProduct.getModel());
        existing.setColor(updatedProduct.getColor());
        existing.setStorage(updatedProduct.getStorage());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setScreenSize(updatedProduct.getScreenSize());
        existing.setNetworkType(updatedProduct.getNetworkType());
        existing.setUpdatedAt(updatedProduct.getUpdatedAt());

        return productRepository.save(existing);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    public PageResponse<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        
        List<Product> searchResults = productRepository.searchProducts(
                criteria.getName(),
                criteria.getBrand(),
                criteria.getCategory(),
                criteria.getPriceMin(),
                criteria.getPriceMax(),
                criteria.getStorage());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), searchResults.size());

        Page<Product> page = new PageImpl<>(searchResults.subList(start, end), pageable, searchResults.size());

        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
    }

    public PageResponse<Product> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
    }
}