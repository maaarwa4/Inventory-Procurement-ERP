package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.enums.Brand;
import com.mon_projet_pfa.backend.enums.Category;
import com.mon_projet_pfa.backend.enums.Color;
import com.mon_projet_pfa.backend.enums.StorageCapacity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumService {

    public List<String> getEnumValues(String enumTypeName) {
        switch (enumTypeName.toLowerCase()) {
            case "brand":
                return Arrays.stream(Brand.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());
            case "category":
                return Arrays.stream(Category.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());
            case "color":
                return Arrays.stream(Color.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());
            case "storage_gb":
                return Arrays.stream(StorageCapacity.values())
                        .map(StorageCapacity::toString)
                        .collect(Collectors.toList());
            default:
                return List.of();
        }
    }
}