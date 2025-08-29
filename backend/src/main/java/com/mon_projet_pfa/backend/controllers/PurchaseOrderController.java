package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.dtos.CreatePurchaseOrderRequestDTO;
import com.mon_projet_pfa.backend.dtos.PurchaseOrderDTO;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

        @Autowired
        private PurchaseOrderService purchaseOrderService;

        // 🔹 Récupérer toutes les commandes (retourne des DTOs)
        @GetMapping
        public ResponseEntity<?> getAllOrders() {
                try {
                        List<PurchaseOrderDTO> orders = purchaseOrderService.getAllOrdersAsDTO();
                        return ResponseEntity.ok(orders);
                } catch (Exception e) {
                        System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur lors de la récupération des commandes",
                                                        e.getMessage()));
                }
        }

        // 🔹 Récupérer une commande par ID
        @GetMapping("/{id}")
        public ResponseEntity<?> getOrderById(@PathVariable Long id) {
                try {
                        return purchaseOrderService.getOrderByIdAsDTO(id)
                                        .map(ResponseEntity::ok)
                                        .orElse(ResponseEntity.notFound().build());
                } catch (Exception e) {
                        System.err.println(
                                        "Erreur lors de la récupération de la commande " + id + ": " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur lors de la récupération de la commande",
                                                        e.getMessage()));
                }
        }

        // 🔹 NOUVELLE MÉTHODE : Créer une commande depuis le frontend (avec IDs
        // simples)
        @PostMapping("/create-from-frontend")
        public ResponseEntity<?> createOrderFromFrontend(@RequestBody CreatePurchaseOrderRequestDTO requestDTO) {
                try {
                        // Validation des données
                        Map<String, String> validationErrors = validateCreateRequest(requestDTO);
                        if (!validationErrors.isEmpty()) {
                                return ResponseEntity.badRequest().body(validationErrors);
                        }

                        // Création de la commande
                        PurchaseOrderDTO created = purchaseOrderService.createOrderFromFrontendRequest(requestDTO);

                        return ResponseEntity.ok(created);

                } catch (RuntimeException e) {
                        System.err.println("Erreur métier lors de la création: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.badRequest()
                                        .body(createErrorResponse("Erreur lors de la création de la commande",
                                                        e.getMessage()));
                } catch (Exception e) {
                        System.err.println("Erreur technique lors de la création: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur interne du serveur", e.getMessage()));
                }
        }

        // 🔹 ANCIENNE MÉTHODE : Créer une commande avec DTO complet (maintenue pour
        // compatibilité)
        @PostMapping
        public ResponseEntity<?> createOrder(@RequestBody PurchaseOrderDTO orderDTO) {
                try {
                        // Validation préliminaire
                        Map<String, String> validationErrors = validatePurchaseOrderDTO(orderDTO);
                        if (!validationErrors.isEmpty()) {
                                return ResponseEntity.badRequest().body(validationErrors);
                        }

                        PurchaseOrderDTO created = purchaseOrderService.createOrderFromDTO(orderDTO);
                        return ResponseEntity.ok(created);

                } catch (RuntimeException e) {
                        System.err.println("Erreur lors de la création de la commande: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.badRequest()
                                        .body(createErrorResponse("Erreur lors de la création", e.getMessage()));
                } catch (Exception e) {
                        System.err.println("Erreur interne: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur interne du serveur", e.getMessage()));
                }
        }

        // 🔹 Mettre à jour une commande
        @PutMapping("/{id}")
        public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody PurchaseOrderDTO orderDTO) {
                try {
                        PurchaseOrderDTO updated = purchaseOrderService.updateOrderFromDTO(id, orderDTO);
                        return ResponseEntity.ok(updated);
                } catch (RuntimeException e) {
                        System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.badRequest()
                                        .body(createErrorResponse("Erreur lors de la mise à jour", e.getMessage()));
                } catch (Exception e) {
                        System.err.println("Erreur interne: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur interne du serveur", e.getMessage()));
                }
        }

        // 🔹 Supprimer une commande
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
                try {
                        purchaseOrderService.deleteOrder(id);
                        return ResponseEntity.noContent().build();
                } catch (RuntimeException e) {
                        return ResponseEntity.notFound().build();
                } catch (Exception e) {
                        System.err.println("Erreur lors de la suppression: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(createErrorResponse("Erreur interne du serveur", e.getMessage()));
                }
        }

        // 🔹 Télécharger le PDF d'une commande
        @GetMapping("/{id}/pdf")
        public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
                try {
                        byte[] pdfContent = purchaseOrderService.generatePdf(id);
                        return ResponseEntity.ok()
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .header("Content-Type", "application/pdf")
                                        .header("Content-Disposition", "attachment; filename=commande-" + id + ".pdf")
                                        .body(pdfContent);
                } catch (RuntimeException e) {
                        return ResponseEntity.notFound().build();
                } catch (Exception e) {
                        System.err.println("Erreur PDF: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        // ================================================
        // MÉTHODES UTILITAIRES DE VALIDATION
        // ================================================

        /**
         * Valide une requête de création depuis le frontend
         */
        private Map<String, String> validateCreateRequest(CreatePurchaseOrderRequestDTO requestDTO) {
                Map<String, String> errors = new HashMap<>();

                if (requestDTO == null) {
                        errors.put("general", "Le corps de la requête ne peut pas être vide");
                        return errors;
                }

                if (requestDTO.getSupplierId() == null) {
                        errors.put("supplierId", "Un fournisseur doit être sélectionné");
                }

                if (!requestDTO.hasProducts()) {
                        errors.put("products", "Au moins un produit doit être sélectionné");
                }

                // ✅ Ici on accepte tout statut non vide
                if (requestDTO.getStatus() == null || requestDTO.getStatus().trim().isEmpty()) {
                        errors.put("status", "Un statut doit être défini");
                }

                return errors;
        }

        /**
         * Valide un DTO de commande complet (ancienne méthode)
         */
        private Map<String, String> validatePurchaseOrderDTO(PurchaseOrderDTO orderDTO) {
                Map<String, String> errors = new HashMap<>();

                if (orderDTO == null) {
                        errors.put("general", "Le corps de la requête ne peut pas être vide");
                        return errors;
                }

                if (orderDTO.getSupplier() == null || orderDTO.getSupplier().getId() == null) {
                        errors.put("supplier", "Un fournisseur avec un ID valide est requis");
                }

                if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
                        errors.put("items", "Au moins un item est requis");
                } else {
                        for (int i = 0; i < orderDTO.getItems().size(); i++) {
                                var item = orderDTO.getItems().get(i);
                                if (item.getProduct() == null || item.getProduct().getId() == null) {
                                        errors.put("item_" + i + "_product", "L'item " + (i + 1)
                                                        + " doit avoir un produit avec un ID valide");
                                }
                                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                                        errors.put("item_" + i + "_quantity", "L'item " + (i + 1)
                                                        + " doit avoir une quantité supérieure à 0");
                                }
                                if (item.getUnitPrice() == null || item.getUnitPrice() <= 0) {
                                        errors.put("item_" + i + "_price", "L'item " + (i + 1)
                                                        + " doit avoir un prix unitaire supérieur à 0");
                                }
                        }
                }

                if (orderDTO.getStatus() == null || orderDTO.getStatus().trim().isEmpty()) {
                        errors.put("status", "Un statut doit être défini");
                }

                return errors;
        }

        /**
         * Crée une réponse d'erreur standardisée
         */
        private Map<String, Object> createErrorResponse(String message, String details) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", message);
                error.put("details", details);
                error.put("timestamp", System.currentTimeMillis());
                return error;
        }
}