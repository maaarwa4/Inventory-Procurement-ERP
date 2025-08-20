// src/main/java/com/mon_projet_pfa/backend/dtos/ProductCreateDTO.java
package com.mon_projet_pfa.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    private String name;
    private String description;
    private String brand; // enum Brand, uppercase e.g. "APPLE"
    private String category; // enum Category, uppercase e.g. "SMARTPHONE"
    private BigDecimal price;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    private String color; // enum Color, uppercase or null
    private String storage; // enum StorageCapacity, uppercase or null

    @JsonProperty("image_url")
    private String imageUrl;

    private String model;

    @JsonProperty("screen_size")
    private String screenSize;

    @JsonProperty("network_type")
    private String networkType;

    @JsonProperty("supplier_id")
    private Long supplierId; // REQUIRED
}
