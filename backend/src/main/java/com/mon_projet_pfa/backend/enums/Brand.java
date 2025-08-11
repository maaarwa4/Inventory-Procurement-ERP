package com.mon_projet_pfa.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Brand {
    APPLE, SAMSUNG, HUAWEI, OPPO, XIAOMI, REALME, NOKIA, LG, SONY, MOTOROLA, ASUS, HONOR, GOOGLE, INFINIX, ONEPLUS;


        @JsonCreator
    public static Brand fromString(String value) {
        return Brand.valueOf(value.toUpperCase());
    }
}
