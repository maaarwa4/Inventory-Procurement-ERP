package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
        List<Product> findBySupplier_Id(Long supplierId);

        // Méthode de recherche personnalisée avec requête native corrigée
        @Query("SELECT p FROM Product p WHERE " +
                        "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
                        "(:brand IS NULL OR :brand = '' OR p.brand = :brand) AND " +
                        "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
                        "(:priceMin IS NULL OR p.price >= :priceMin) AND " +
                        "(:priceMax IS NULL OR p.price <= :priceMax) AND " +
                        "(:storage IS NULL OR :storage = '' OR p.storage = :storage)")
        List<Product> searchProducts(
                        @Param("name") String name,
                        @Param("brand") String brand,
                        @Param("category") String category,
                        @Param("priceMin") Double priceMin,
                        @Param("priceMax") Double priceMax,
                        @Param("storage") String storage);
}
