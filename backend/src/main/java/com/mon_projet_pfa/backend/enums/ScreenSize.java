package com.mon_projet_pfa.backend.enums;


public enum ScreenSize {
    INCH_4_7("4.7"),
    INCH_5_5("5.5"), 
    INCH_6_1("6.1"),
    INCH_6_5("6.5"),
    INCH_6_7("6.7"),
    INCH_7_0("7.0");

    private final String value;

    ScreenSize(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Méthode pour convertir depuis la valeur String
    public static ScreenSize fromString(String value) {
        for (ScreenSize size : ScreenSize.values()) {
            if (size.value.equals(value)) {
                return size;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}