package com.mon_projet_pfa.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Color {
    BLACK, WHITE, SILVER, GOLD, BLUE, RED, GREEN, PURPLE, YELLOW, GRAY;
      @JsonCreator
    public static Color fromString(String value) {
        return Color.valueOf(value.toUpperCase());
    }
}
