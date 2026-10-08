package com.mon_projet_pfa.backend.repositories;
import com.mon_projet_pfa.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Additional query methods can be defined here if needed

}
