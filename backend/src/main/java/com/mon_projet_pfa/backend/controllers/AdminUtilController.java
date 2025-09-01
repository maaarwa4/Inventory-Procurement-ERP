package com.mon_projet_pfa.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUtilController {

    @Autowired
    private PasswordEncoder passwordEncoder;

 
    @PostMapping("/generate-password")
    public ResponseEntity<?> generatePassword(@RequestBody Map<String, String> request) {
        try {
            String plainPassword = request.get("password");

            if (plainPassword == null || plainPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le mot de passe ne peut pas être vide"));
            }

            String hashedPassword = passwordEncoder.encode(plainPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("plainPassword", plainPassword);
            response.put("hashedPassword", hashedPassword);
            response.put("sqlQuery", String.format(
                    "UPDATE admin SET password = '%s' WHERE email = 'marwa@test.com';",
                    hashedPassword));

            System.out.println("Password généré pour: " + plainPassword);
            System.out.println("Hash: " + hashedPassword);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Erreur génération password: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la génération", "details", e.getMessage()));
        }
    }

    /**
     * Endpoint pour tester la validation d'un mot de passe
     */
    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        try {
            String plainPassword = request.get("plainPassword");
            String hashedPassword = request.get("hashedPassword");

            if (plainPassword == null || hashedPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "plainPassword et hashedPassword requis"));
            }

            boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("plainPassword", plainPassword);
            response.put("hashedPassword", hashedPassword);
            response.put("matches", matches);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors du test", "details", e.getMessage()));
        }
    }
}