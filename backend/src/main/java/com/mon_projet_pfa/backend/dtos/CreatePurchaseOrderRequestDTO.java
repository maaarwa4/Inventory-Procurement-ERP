package com.mon_projet_pfa.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO spécialement conçu pour recevoir les données du frontend
 * qui envoie des IDs simples au lieu d'objets complets
 */
public class CreatePurchaseOrderRequestDTO {
    
    @JsonProperty("creationDate")
    private String creationDate;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("supplierId")
    private Long supplierId;
    
    @JsonProperty("selectedProductIds")
    private List<Long> selectedProductIds;
    
    // Optionnel : si vous voulez permettre des quantités personnalisées
    @JsonProperty("quantities")
    private List<Integer> quantities;
    
    // Constructeurs
    public CreatePurchaseOrderRequestDTO() {}
    
    public CreatePurchaseOrderRequestDTO(String creationDate, String status, 
                                       Long supplierId, List<Long> selectedProductIds) {
        this.creationDate = creationDate;
        this.status = status;
        this.supplierId = supplierId;
        this.selectedProductIds = selectedProductIds;
    }
    
    // Getters et Setters
    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    
    public List<Long> getSelectedProductIds() { return selectedProductIds; }
    public void setSelectedProductIds(List<Long> selectedProductIds) { 
        this.selectedProductIds = selectedProductIds; 
    }
    
    public List<Integer> getQuantities() { return quantities; }
    public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }
    
    // Méthodes utilitaires
    public boolean hasProducts() {
        return selectedProductIds != null && !selectedProductIds.isEmpty();
    }
    
    public int getProductCount() {
        return selectedProductIds != null ? selectedProductIds.size() : 0;
    }
}