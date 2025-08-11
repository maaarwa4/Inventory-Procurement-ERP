package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.dtos.ProductSearchCriteria;
import com.mon_projet_pfa.backend.models.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchProducts(ProductSearchCriteria criteria);
}
