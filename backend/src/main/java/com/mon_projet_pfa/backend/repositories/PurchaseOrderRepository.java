package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.enums.purchase_order_status;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    /**
     * Trouve toutes les commandes d'un fournisseur spécifique
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId ORDER BY po.creationDate DESC")
    List<PurchaseOrder> findBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * Trouve toutes les commandes avec un statut spécifique
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status ORDER BY po.creationDate DESC")
    List<PurchaseOrder> findByStatus(@Param("status") purchase_order_status status);

    /**
     * Trouve toutes les commandes créées entre deux dates
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.creationDate BETWEEN :startDate AND :endDate ORDER BY po.creationDate DESC")
    List<PurchaseOrder> findByCreationDateBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Compte le nombre de commandes par statut
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status = :status")
    Long countByStatus(@Param("status") purchase_order_status status);

    /**
     * Calcule le montant total de toutes les commandes
     */
    @Query("SELECT COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po")
    Double getTotalAmount();

    /**
     * Calcule le montant total des commandes avec un statut spécifique
     */
    @Query("SELECT COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.status = :status")
    Double getTotalAmountByStatus(@Param("status") purchase_order_status status);

    /**
     * Trouve toutes les commandes avec leurs items (évite le lazy loading)
     */
    @Query("SELECT DISTINCT po FROM PurchaseOrder po LEFT JOIN FETCH po.items LEFT JOIN FETCH po.supplier ORDER BY po.creationDate DESC")
    List<PurchaseOrder> findAllWithItems();

    /**
     * Trouve une commande par ID avec ses items
     */
    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items LEFT JOIN FETCH po.supplier WHERE po.id = :id")
    PurchaseOrder findByIdWithItems(@Param("id") Long id);

    /**
     * Recherche par nom de fournisseur ou numéro de commande
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE " +
            "LOWER(CONCAT(po.supplier.firstName, ' ', po.supplier.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            +
            "OR CAST(po.id AS string) LIKE CONCAT('%', :searchTerm, '%') " +
            "ORDER BY po.creationDate DESC")
    List<PurchaseOrder> searchOrders(@Param("searchTerm") String searchTerm);
}