package com.mon_projet_pfa.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    @JsonProperty("firstName")
    private String firstName;

    @Column(name = "last_name", length = 100)
    @JsonProperty("lastName")
    private String lastName;

    @Column(name = "company_name", length = 100)
    @JsonProperty("companyName")
    private String companyName;

    @Column(length = 100)
    @JsonProperty("phone")
    @Nullable
    private String phone;

    @Column(length = 100)
    @JsonProperty("address")
    private String city;

    @Email(message = "doit être une adresse email valide")
    @Column(length = 100)
    @JsonProperty("email")
    private String email;

    @Column(length = 100)
    @JsonProperty("country")
    private String country;

    @Column(name = "is_active")
    @JsonProperty("isActive")
    private Boolean isActive = true;
}