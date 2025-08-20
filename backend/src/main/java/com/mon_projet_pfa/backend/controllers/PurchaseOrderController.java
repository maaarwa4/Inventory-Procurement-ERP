package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.dtos.PurchaseOrderDTO;
import com.mon_projet_pfa.backend.enums.purchase_order_status;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.repositories.PurchaseOrderRepository;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mon_projet_pfa.backend.services.PurchaseOrderPdfService;

import java.io.IOException;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

        private final PurchaseOrderRepository purchaseOrderRepository;
        private final SupplierRepository supplierRepository;
        private final ProductRepository productRepository;

        @Autowired
        private PurchaseOrderPdfService purchaseOrderPdfService;

        public PurchaseOrderController(PurchaseOrderRepository purchaseOrderRepository,
                        SupplierRepository supplierRepository,
                        ProductRepository productRepository) {
                this.purchaseOrderRepository = purchaseOrderRepository;
                this.supplierRepository = supplierRepository;
                this.productRepository = productRepository;
        }

        // ✅ Récupérer toutes les commandes
        @GetMapping
        public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
                List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
                return ResponseEntity.ok(orders);
        }

        // ✅ Récupérer une commande par ID
        @GetMapping("/{id}")
        public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable Integer id) {
                PurchaseOrder order = purchaseOrderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
                return ResponseEntity.ok(order);
        }

        // ✅ Créer une commande (avec calcul automatique du totalAmount)
        @PostMapping
        public ResponseEntity<PurchaseOrder> createOrder(@RequestBody PurchaseOrderDTO dto) {
                PurchaseOrder order = new PurchaseOrder();
                order.setCreationDate(LocalDate.parse(dto.creationDate));
                order.setStatus(purchase_order_status.valueOf(dto.status));
                order.setQuantity(dto.quantity);

                Supplier supplier = supplierRepository.findById(dto.supplierId)
                                .orElseThrow(() -> new RuntimeException("Supplier not found"));
                Product product = productRepository.findById(dto.productId.intValue())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                order.setSupplier(supplier);
                order.setProduct(product);

                // 💡 Calcul automatique du totalAmount
                double totalAmount = product.getPrice().multiply(BigDecimal.valueOf(dto.quantity)).doubleValue();
                order.setTotalAmount(totalAmount);

                PurchaseOrder savedOrder = purchaseOrderRepository.save(order);
                return ResponseEntity.ok(savedOrder);
        }

        // ✅ Mettre à jour une commande
        @PutMapping("/{id}")
        public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable int id, @RequestBody PurchaseOrderDTO dto) {
                PurchaseOrder order = purchaseOrderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

                order.setCreationDate(LocalDate.parse(dto.creationDate));
                order.setStatus(purchase_order_status.valueOf(dto.status));
                order.setQuantity(dto.quantity);

                Supplier supplier = supplierRepository.findById(dto.supplierId)
                                .orElseThrow(() -> new RuntimeException("Supplier not found"));
                Product product = productRepository.findById(dto.productId.intValue())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                order.setSupplier(supplier);
                order.setProduct(product);

                // 💡 Recalcul automatique du totalAmount
                double totalAmount = product.getPrice().multiply(BigDecimal.valueOf(dto.quantity)).doubleValue();
                order.setTotalAmount(totalAmount);

                PurchaseOrder updatedOrder = purchaseOrderRepository.save(order);
                return ResponseEntity.ok(updatedOrder);
        }

        // ✅ Supprimer une commande
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
                if (!purchaseOrderRepository.existsById(id)) {
                        throw new RuntimeException("Commande introuvable");
                }
                purchaseOrderRepository.deleteById(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}/pdf")
        public ResponseEntity<byte[]> downloadPurchaseOrderPdf(@PathVariable Long id) throws IOException {
                PurchaseOrder order = purchaseOrderRepository.findById(id.intValue())
                                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
                byte[] pdf = purchaseOrderPdfService.generatePurchaseOrderPdf(
                                order.getSupplier().getFirstName() + " " + order.getSupplier().getLastName(),
                                order.getProduct().getName(),
                                order.getQuantity(),
                                order.getTotalAmount(),
                                order.getStatus().name());

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=bon_achat_" + id + ".pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdf);
        }
}
