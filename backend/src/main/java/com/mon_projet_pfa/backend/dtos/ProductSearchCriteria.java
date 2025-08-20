package com.mon_projet_pfa.backend.dtos;

public class ProductSearchCriteria {
    private String name;
    private String brand;
    private String category;
    private Double priceMin;
    private Double priceMax;
    private String storage;
    private String model;

    // Constructeurs
    public ProductSearchCriteria() {
    }

    public ProductSearchCriteria(String name, String brand, String category,
            Double priceMin, Double priceMax, String storage, String model) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.model = model;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.storage = storage;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
    }

    public Double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    // Méthodes utilitaires pour la validation
    public boolean hasNameFilter() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasBrandFilter() {
        return brand != null && !brand.trim().isEmpty();
    }

    public boolean hasCategoryFilter() {
        return category != null && !category.trim().isEmpty();
    }

    public boolean hasPriceRangeFilter() {
        return priceMin != null || priceMax != null;
    }

    public boolean hasStorageFilter() {
        return storage != null && !storage.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ProductSearchCriteria{" +
                "name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", storage='" + storage + '\'' +
                '}';
    }
}