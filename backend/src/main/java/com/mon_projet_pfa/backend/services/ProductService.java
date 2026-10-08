package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        updatedProduct.setId(existing.getId());
        return productRepository.save(updatedProduct);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

}
