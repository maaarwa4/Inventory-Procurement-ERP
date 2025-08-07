package com.mon_projet_pfa.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnumService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getEnumValues(String enumTypeName) {
        String sql = "SELECT e.enumlabel " +
                "FROM pg_type t " +
                "JOIN pg_enum e ON t.oid = e.enumtypid " +
                "WHERE t.typname = ?";
        return jdbcTemplate.query(sql, ps -> ps.setString(1, enumTypeName), (rs, rowNum) -> rs.getString("enumlabel"));
    }
}
