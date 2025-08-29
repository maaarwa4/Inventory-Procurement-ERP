package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.dtos.CreatePurchaseOrderRequestDTO;
import com.mon_projet_pfa.backend.dtos.OrderItemDTO;
import com.mon_projet_pfa.backend.dtos.PurchaseOrderDTO;
import com.mon_projet_pfa.backend.enums.purchase_order_status;
import com.mon_projet_pfa.backend.models.OrderItem;
import com.mon_projet_pfa.backend.models.Product;
import com.mon_projet_pfa.backend.models.PurchaseOrder;
import com.mon_projet_pfa.backend.models.Supplier;
import com.mon_projet_pfa.backend.repositories.OrderItemRepository;
import com.mon_projet_pfa.backend.repositories.ProductRepository;
import com.mon_projet_pfa.backend.repositories.PurchaseOrderRepository;
import com.mon_projet_pfa.backend.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mon_projet_pfa.backend.services.PurchaseOrderPdfService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PurchaseOrderPdfService purchaseOrderPdfService;

    // ================================================
    // CORRECTION 1: Créer une commande avec le statut choisi (pas forcément
    // PENDING)
    // ================================================

    /**
     * Crée une nouvelle commande à partir d'un DTO complet - STATUT RESPECTÉ
     */
    public PurchaseOrderDTO createOrderFromDTO(PurchaseOrderDTO dto) {
        try {
            System.out.println("🔄 Création commande avec statut: " + dto.getStatus());

            // Conversion du DTO vers l'entité
            PurchaseOrder order = convertFromDTO(dto);

            // 🔥 CORRECTION: Ne plus forcer PENDING - Respecter le statut choisi
            if (dto.getCreationDate() == null || dto.getCreationDate().isEmpty()) {
                order.setCreationDate(LocalDate.now());
            } else {
                order.setCreationDate(LocalDate.parse(dto.getCreationDate()));
            }

            // 🔥 CORRECTION: Respecter le statut du DTO au lieu de forcer PENDING
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                try {
                    order.setStatus(purchase_order_status.valueOf(dto.getStatus().toUpperCase()));
                    System.out.println("✅ Statut défini: " + dto.getStatus());
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Statut invalide, utilisation de PENDING par défaut");
                    order.setStatus(purchase_order_status.PENDING);
                }
            } else {
                order.setStatus(purchase_order_status.PENDING);
            }

            // Sauvegarde de la commande
            PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

            System.out.println("✅ Commande créée avec ID: " + savedOrder.getId() +
                    " et statut: " + savedOrder.getStatus());

            // Retour du DTO
            return convertToDTO(savedOrder);
        } catch (Exception e) {
            System.err.println("❌ Erreur création: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la commande: " + e.getMessage());
        }
    }

    public PurchaseOrderDTO createOrderFromFrontendRequest(CreatePurchaseOrderRequestDTO requestDTO) {
        try {
            System.out.println("Début création commande depuis frontend...");
            System.out.println("Données reçues: SupplierId=" + requestDTO.getSupplierId() +
                    ", Products=" + requestDTO.getSelectedProductIds() +
                    ", Status=" + requestDTO.getStatus());

            // 1. Validation et récupération du fournisseur
            Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException(
                            "Fournisseur non trouvé avec l'ID: " + requestDTO.getSupplierId()));

            System.out.println("Fournisseur trouvé: " + supplier.getFirstName() + " " + supplier.getLastName());

            // 2. Validation et récupération des produits
            List<Product> selectedProducts = new ArrayList<>();
            for (Long productId : requestDTO.getSelectedProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException(
                                "Produit non trouvé avec l'ID: " + productId));
                selectedProducts.add(product);
            }

            System.out.println(selectedProducts.size() + " produits trouvés");

            PurchaseOrder order = new PurchaseOrder();

            // Date de création
            if (requestDTO.getCreationDate() != null && !requestDTO.getCreationDate().isEmpty()) {
                try {
                    order.setCreationDate(LocalDate.parse(requestDTO.getCreationDate()));
                } catch (Exception e) {
                    System.out.println("Date invalide, utilisation de la date actuelle");
                    order.setCreationDate(LocalDate.now());
                }
            } else {
                order.setCreationDate(LocalDate.now());
            }

            // 🔥 CORRECTION: Respecter le statut demandé
            try {
                order.setStatus(purchase_order_status.valueOf(requestDTO.getStatus().toUpperCase()));
                System.out.println("✅ Statut défini: " + requestDTO.getStatus());
            } catch (IllegalArgumentException e) {
                System.out.println("Statut invalide, utilisation de PENDING par défaut");
                order.setStatus(purchase_order_status.PENDING);
            }

            // Fournisseur
            order.setSupplier(supplier);

            // 4. Création des items avec quantité par défaut = 1 et prix du produit
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < selectedProducts.size(); i++) {
                Product product = selectedProducts.get(i);

                // Quantité : soit depuis la liste des quantités, soit 1 par défaut
                Integer quantity = 1;
                if (requestDTO.getQuantities() != null &&
                        i < requestDTO.getQuantities().size() &&
                        requestDTO.getQuantities().get(i) > 0) {
                    quantity = requestDTO.getQuantities().get(i);
                }

                // Prix unitaire = prix du produit depuis la base de données
                BigDecimal unitPrice = product.getPrice();

                // Vérification du stock
                if (product.getStockQuantity() < quantity) {
                    throw new RuntimeException(
                            "Stock insuffisant pour le produit '" + product.getName() + "'. " +
                                    "Stock disponible: " + product.getStockQuantity() +
                                    ", quantité demandée: " + quantity);
                }

                OrderItem item = new OrderItem();
                item.setPurchaseOrder(order);
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);

                items.add(item);

                System.out.println("Item créé: " + product.getName() +
                        " x" + quantity + " à " + unitPrice + "€");
            }

            order.setItems(items);

            // 5. Sauvegarde
            PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

            System.out.println("Commande créée avec l'ID: " + savedOrder.getId() +
                    ", Total: " + savedOrder.getTotalAmount() + "€");

            // 6. Conversion en DTO et retour
            return convertToDTO(savedOrder);

        } catch (Exception e) {
            System.err.println("Erreur lors de la création depuis frontend: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la commande: " + e.getMessage(), e);
        }
    }

    // ================================================
    // MÉTHODES UTILITAIRES POUR LE FRONTEND
    // ================================================

    /**
     * Récupère la liste des fournisseurs pour le dropdown
     */
    public List<Supplier> getAllSuppliers() {
        try {
            return supplierRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des fournisseurs: " + e.getMessage());
        }
    }

    /**
     * Récupère la liste des produits pour les checkboxes
     */
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des produits: " + e.getMessage());
        }
    }

    // ================================================
    // MÉTHODES EXISTANTES : Récupération des données en DTO
    // ================================================

    /**
     * Récupère toutes les commandes et les convertit en DTO
     */
    public List<PurchaseOrderDTO> getAllOrdersAsDTO() {
        try {
            List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
            return orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
    }

    /**
     * Récupère une commande par ID et la convertit en DTO
     */
    public Optional<PurchaseOrderDTO> getOrderByIdAsDTO(Long id) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(this::convertToDTO);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération de la commande " + id + ": " + e.getMessage());
        }
    }

    // ================================================
    // CORRECTION 2: Modification améliorée
    // ================================================

    /**
     * Met à jour une commande existante à partir d'un DTO - VERSION CORRIGÉE
     */
    public PurchaseOrderDTO updateOrderFromDTO(Long id, PurchaseOrderDTO dto) {
        try {
            System.out.println("🔄 Modification commande ID: " + id);
            System.out.println("🔄 Nouvelles données: " + dto);

            // Récupération de la commande existante
            PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

            // 🔥 CORRECTION: Mise à jour de la date si fournie
            if (dto.getCreationDate() != null && !dto.getCreationDate().isEmpty()) {
                try {
                    existingOrder.setCreationDate(LocalDate.parse(dto.getCreationDate()));
                    System.out.println("✅ Date mise à jour: " + dto.getCreationDate());
                } catch (Exception e) {
                    System.out.println("⚠️ Date invalide, conservation de l'ancienne date");
                }
            }

            // Mise à jour du statut si fourni
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                try {
                    existingOrder.setStatus(purchase_order_status.valueOf(dto.getStatus().toUpperCase()));
                    System.out.println("✅ Statut mis à jour: " + dto.getStatus());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Statut invalide: " + dto.getStatus());
                }
            }

            // Mise à jour du fournisseur si fourni
            if (dto.getSupplier() != null && dto.getSupplier().getId() != null) {
                Supplier supplier = supplierRepository.findById(dto.getSupplier().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Fournisseur non trouvé avec l'ID: " + dto.getSupplier().getId()));
                existingOrder.setSupplier(supplier);
                System.out
                        .println("✅ Fournisseur mis à jour: " + supplier.getFirstName() + " " + supplier.getLastName());
            }

            // Mise à jour des items si fournis
            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                // Suppression des anciens items
                existingOrder.getItems().clear();

                // Ajout des nouveaux items
                List<OrderItem> newItems = dto.getItems().stream()
                        .map(itemDTO -> createOrderItemFromDTO(itemDTO, existingOrder))
                        .collect(Collectors.toList());

                existingOrder.setItems(newItems);
                System.out.println("✅ Items mis à jour: " + newItems.size() + " items");
            }

            // Sauvegarde et retour
            PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);
            System.out.println("✅ Commande mise à jour avec succès: ID=" + updatedOrder.getId());

            return convertToDTO(updatedOrder);

        } catch (Exception e) {
            System.err.println("❌ Erreur modification: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la commande: " + e.getMessage());
        }
    }

    // ================================================
    // MÉTHODES DE CONVERSION DTO ↔ Entity
    // ================================================

    /**
     * Convertit une entité PurchaseOrder en DTO
     */
    private PurchaseOrderDTO convertToDTO(PurchaseOrder order) {
        try {
            // Conversion des items en DTO
            List<OrderItemDTO> itemDTOs = order.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());

            // Création du DTO principal
            PurchaseOrderDTO dto = new PurchaseOrderDTO();
            dto.setId(order.getId());
            dto.setCreationDate(order.getCreationDate() != null ? order.getCreationDate().toString() : null);
            dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
            dto.setSupplier(order.getSupplier());
            dto.setItems(itemDTOs);
            dto.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion vers DTO: " + e.getMessage());
        }
    }

    /**
     * Convertit un DTO en entité PurchaseOrder
     */
    private PurchaseOrder convertFromDTO(PurchaseOrderDTO dto) {
        try {
            PurchaseOrder order = new PurchaseOrder();

            // Définition du statut
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                try {
                    order.setStatus(purchase_order_status.valueOf(dto.getStatus().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Statut invalide: " + dto.getStatus());
                }
            }

            // Récupération et validation du fournisseur
            if (dto.getSupplier() == null || dto.getSupplier().getId() == null) {
                throw new RuntimeException("Un fournisseur est requis pour créer une commande");
            }

            Supplier supplier = supplierRepository.findById(dto.getSupplier().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Fournisseur non trouvé avec l'ID: " + dto.getSupplier().getId()));
            order.setSupplier(supplier);

            // Conversion des items si fournis
            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                List<OrderItem> items = dto.getItems().stream()
                        .map(itemDTO -> createOrderItemFromDTO(itemDTO, order))
                        .collect(Collectors.toList());
                order.setItems(items);
            }

            return order;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion depuis DTO: " + e.getMessage());
        }
    }

    // ================================================
    // MÉTHODES UTILITAIRES POUR LES OrderItems
    // ================================================

    /**
     * Convertit un OrderItem en OrderItemDTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProduct(item.getProduct());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0);
        return dto;
    }

    /**
     * Crée un OrderItem à partir d'un OrderItemDTO
     */
    private OrderItem createOrderItemFromDTO(OrderItemDTO itemDTO, PurchaseOrder order) {
        try {
            // Validation des données requises
            if (itemDTO.getProduct() == null || itemDTO.getProduct().getId() == null) {
                throw new RuntimeException("Un produit est requis pour chaque item de commande");
            }

            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new RuntimeException("La quantité doit être supérieure à 0");
            }

            if (itemDTO.getUnitPrice() == null || itemDTO.getUnitPrice() <= 0) {
                throw new RuntimeException("Le prix unitaire doit être supérieur à 0");
            }

            // Récupération du produit
            Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produit non trouvé avec l'ID: " + itemDTO.getProduct().getId()));

            // Création de l'OrderItem
            OrderItem item = new OrderItem();
            item.setPurchaseOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(BigDecimal.valueOf(itemDTO.getUnitPrice()));

            return item;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'item: " + e.getMessage());
        }
    }

    // ================================================
    // MÉTHODES ANCIENNES (maintenues pour compatibilité)
    // ================================================

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    public PurchaseOrder createOrder(PurchaseOrder order) {
        System.out.println("🚀 Statut reçu du front: " + order.getStatus());
        order.setCreationDate(LocalDate.now());

        if (order.getStatus() == null) {
            order.setStatus(purchase_order_status.PENDING);
        }

        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder updateOrder(Long id, PurchaseOrder updatedOrder) {
        return purchaseOrderRepository.findById(id)
                .map(order -> {
                    order.setStatus(updatedOrder.getStatus());
                    order.setSupplier(updatedOrder.getSupplier());
                    order.setItems(updatedOrder.getItems());
                    return purchaseOrderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    public void deleteOrder(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + id);
        }
        purchaseOrderRepository.deleteById(id);
    }

    // ================================================
    // GÉNÉRATION PDF
    // ================================================

    public byte[] generatePdf(Long orderId) {
        try {
            PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + orderId));

            if (order.getItems().isEmpty()) {
                throw new RuntimeException("La commande est vide");
            }

            calculateTotalAmount(order);

            String supplierFullName = order.getSupplier().getCompanyName() + " (" +
                    order.getSupplier().getFirstName() + " " +
                    order.getSupplier().getLastName() + ")";

            return purchaseOrderPdfService.generatePurchaseOrderPdf(
                    supplierFullName,
                    order.getItems(),
                    order.getStatus().name());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    private void calculateTotalAmount(PurchaseOrder order) {
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice().doubleValue() * item.getQuantity())
                .sum();
        order.setTotalAmount(BigDecimal.valueOf(total));
        purchaseOrderRepository.save(order);
    }
}