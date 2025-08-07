package com.mon_projet_pfa.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mon_projet_pfa.backend.repositories.PurchaseOrderRepository;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.models.Supplier;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getOrderById(int id) {
        return purchaseOrderRepository.findById(id);
    }

    public PurchaseOrder createOrder(PurchaseOrder order) {
        // Charger produit et fournisseur pour calculer le montant
        Product product = productRepository.findById(order.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Supplier supplier = supplierRepository.findById(order.getSupplier().getId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

        // Associer produit et fournisseur
        order.setProduct(product);
        order.setSupplier(supplier);

        // Calcul automatique du montant total
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())).doubleValue());

        return purchaseOrderRepository.save(order);
    }

    public void deleteOrder(int id) {
        purchaseOrderRepository.deleteById(id);
    }

    public PurchaseOrder updateOrder(int id, PurchaseOrder updatedOrder) {
        return purchaseOrderRepository.findById(id).map(order -> {
            order.setCreationDate(updatedOrder.getCreationDate());
            order.setStatus(updatedOrder.getStatus());
            order.setQuantity(updatedOrder.getQuantity());

            // Recalculer le montant à partir du prix du produit
            Product product = productRepository.findById(updatedOrder.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));
            order.setProduct(product);
            order.setSupplier(updatedOrder.getSupplier());
            order.setTotalAmount(
                    product.getPrice().multiply(BigDecimal.valueOf(updatedOrder.getQuantity())).doubleValue());

            return purchaseOrderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("Commande introuvable"));
    }
}
