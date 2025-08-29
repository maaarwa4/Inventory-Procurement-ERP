package com.mon_projet_pfa.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mon_projet_pfa.backend.models.OrderItem;
import com.mon_projet_pfa.backend.models.Product;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

public class OrderItemDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("product")
    private Product product;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitPrice")
    private Double unitPrice;

    // Constructeurs
    public OrderItemDTO() {
    }

    // ✅ CORRECTION: Constructeur avec Product au lieu de productId
    public OrderItemDTO(Product product, Integer quantity, Double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Constructeur depuis OrderItem entity
    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.product = orderItem.getProduct();
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice() != null ? orderItem.getUnitPrice().doubleValue() : 0.0;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Méthodes utilitaires
    public Double getTotalPrice() {
        return unitPrice != null && quantity != null ? unitPrice * quantity : 0.0;
    }

    // Conversion vers OrderItem entity
    public OrderItem toEntity() {
        OrderItem item = new OrderItem();
        item.setId(this.id);
        item.setProduct(this.product);
        item.setQuantity(this.quantity);
        item.setUnitPrice(this.unitPrice != null ? BigDecimal.valueOf(this.unitPrice) : BigDecimal.ZERO);
        return item;
    }
}