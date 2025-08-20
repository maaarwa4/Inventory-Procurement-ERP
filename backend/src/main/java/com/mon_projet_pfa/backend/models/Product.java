package com.mon_projet_pfa.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank; // Correction: utiliser jakarta au lieu de javax
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.Category;
import com.mon_projet_pfa.backend.enums.Color;
import com.mon_projet_pfa.backend.enums.StorageCapacity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Suppression de columnDefinition problématique
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Suppression de columnDefinition problématique
    private Category category;

    @Min(0)
    @Column(nullable = false, precision = 10, scale = 2) // Correction de columnDefinition
    private BigDecimal price;

    @Min(0)
    @Column(columnDefinition = "integer default 0")
    @JsonProperty("stock_quantity")
    private Integer stockQuantity = 0; // Valeur par défaut

    @Column(name = "image_url", length = 255)
    @JsonProperty("image_url")
    private String imageUrl;

    @Column(length = 50)
    private String model;

    @Enumerated(EnumType.STRING)
    private Color color; // Suppression de columnDefinition problématique

    @Enumerated(EnumType.STRING)
    private StorageCapacity storage; // Suppression de columnDefinition problématique

    @Column(name = "screen_size", length = 10)
    @JsonProperty("screen_size")
    private String screenSize;

    @Column(name = "network_type", length = 20)
    @JsonProperty("network_type")
    private String networkType;

    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore
    private Supplier supplier;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}