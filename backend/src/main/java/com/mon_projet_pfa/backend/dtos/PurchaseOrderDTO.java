package com.mon_projet_pfa.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mon_projet_pfa.backend.models.Supplier;
import java.util.List;
import java.util.ArrayList;

public class PurchaseOrderDTO {

    private Long id;

    @JsonProperty("creationDate")
    private String creationDate;

    private String status;

    // ✅ CORRECTION: Utiliser l'objet Supplier complet, pas juste l'ID
    @JsonProperty("supplier")
    private Supplier supplier;

    @JsonProperty("totalAmount")
    private Double totalAmount;

    // ✅ CORRECTION: Liste des items avec le bon nom de classe
    @JsonProperty("items")
    private List<OrderItemDTO> items = new ArrayList<>();

    // Constructeurs
    public PurchaseOrderDTO() {
    }

    public PurchaseOrderDTO(Long id, String creationDate, String status, Supplier supplier,
            List<OrderItemDTO> items, Double totalAmount) {
        this.id = id;
        this.creationDate = creationDate;
        this.status = status;
        this.supplier = supplier;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ✅ CORRECTION: Getter/Setter pour l'objet Supplier complet
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    // Méthodes utilitaires
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }
}