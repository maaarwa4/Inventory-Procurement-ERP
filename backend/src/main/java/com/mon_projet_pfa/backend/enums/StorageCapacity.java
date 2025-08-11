package com.mon_projet_pfa.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StorageCapacity {
    GB16, GB32, GB64, GB128, GB256, GB512, TB1;

    @Override
    public String toString() {
        if (this == TB1)
            return "1TB";
        return name().replace("GB", "") + "GB";
    }

    @JsonCreator
    public static StorageCapacity fromString(String value) {
        return StorageCapacity.valueOf(value.toUpperCase());
    }
}