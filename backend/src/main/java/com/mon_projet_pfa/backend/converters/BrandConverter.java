package com.mon_projet_pfa.backend.converters;

import com.mon_projet_pfa.backend.enums.Brand;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BrandConverter implements AttributeConverter<Brand, String> {

    @Override
    public String convertToDatabaseColumn(Brand attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public Brand convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Brand.valueOf(dbData);
    }
}