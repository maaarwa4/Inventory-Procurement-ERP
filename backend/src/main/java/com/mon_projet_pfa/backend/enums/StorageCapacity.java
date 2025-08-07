package com.mon_projet_pfa.backend.enums;

public enum StorageCapacity {
    GB16, GB32, GB64, GB128, GB256, GB512, TB1;

    @Override
    public String toString() {
        if (this == TB1) return "1TB";
        return name().replace("GB", "") + "GB";
    }
}