package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Trouve tous les items d'une commande spécifique
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.purchaseOrder.id = :purchaseOrderId")
    List<OrderItem> findByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);
    
    /**
     * Trouve tous les items contenant un produit spécifique
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    /**
     * Supprime tous les items d'une commande
     */
    @Query("DELETE FROM OrderItem oi WHERE oi.purchaseOrder.id = :purchaseOrderId")
    void deleteByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);
}