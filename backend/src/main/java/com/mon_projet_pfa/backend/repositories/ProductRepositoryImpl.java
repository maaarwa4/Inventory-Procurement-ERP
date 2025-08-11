package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> searchProducts(ProductSearchCriteria criteria) {
        String jpql = "SELECT p FROM Product p WHERE 1=1";
        Map<String, Object> params = new HashMap<>();

        if (criteria.getName() != null && !criteria.getName().isEmpty()) {
            jpql += " AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))";
            params.put("name", criteria.getName());
        }
        if (criteria.getModel() != null && !criteria.getModel().isEmpty()) {
            jpql += " AND LOWER(p.model) LIKE LOWER(CONCAT('%', :model, '%'))";
            params.put("model", criteria.getModel());
        }
        if (criteria.getBrand() != null && !criteria.getBrand().isEmpty()) {
            jpql += " AND p.brand = :brand";
            params.put("brand", Brand.fromString(criteria.getBrand()));
        }
        if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
            jpql += " AND p.category = :category";
            params.put("category", Category.fromString(criteria.getCategory()));
        }
        if (criteria.getPriceMin() != null) {
            jpql += " AND p.price >= :minPrice";
            params.put("minPrice", criteria.getPriceMin());
        }
        if (criteria.getPriceMax() != null) {
            jpql += " AND p.price <= :maxPrice";
            params.put("maxPrice", criteria.getPriceMax());
        }

        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

}
