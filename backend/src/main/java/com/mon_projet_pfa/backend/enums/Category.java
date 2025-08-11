package com.mon_projet_pfa.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    SMARTPHONE, TABLET, LAPTOP, SMARTWATCH, ACCESSORY;
        @JsonCreator
    public static Category fromString(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}
