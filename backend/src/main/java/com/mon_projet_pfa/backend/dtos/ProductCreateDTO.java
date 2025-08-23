package com.mon_projet_pfa.backend.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.Category;
import com.mon_projet_pfa.backend.enums.Color;
import com.mon_projet_pfa.backend.enums.StorageCapacity;
import com.mon_projet_pfa.backend.models.Product;

@Data
public class ProductCreateDTO {
    private String name;
    private String description;
    private String brand;
    private String category;
    private BigDecimal price;
    private String model;
    private String color;
    private String storage;
    private Integer stock_quantity;
    private String image_url;
    private String screen_size;
    private String network_type;
    private Long supplier_id;


}
