package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.models.Admin;
import com.mon_projet_pfa.backend.security.JwtUtil;
import com.mon_projet_pfa.backend.services.AdminService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:4200", "http://127.0.0.1:4200" })
public class AuthController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AdminService adminService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Tentative de connexion pour: {}", request.getEmail());

        try {
            // ✅ Validation des données d'entrée
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                log.warn("Email vide fourni");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email requis", "L'email ne peut pas être vide"));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.warn("Mot de passe vide fourni");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Mot de passe requis", "Le mot de passe ne peut pas être vide"));
            }

            // ✅ Recherche de l'admin
            log.info("Recherche de l'admin avec email: {}", request.getEmail());
            Admin admin = adminService.findByEmail(request.getEmail().trim().toLowerCase());

            if (admin == null) {
                log.warn("Admin non trouvé pour email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Credentials invalides", "Email ou mot de passe incorrect"));
            }

            log.info("Admin trouvé: {}", admin.getEmail());

            // ✅ Vérification du mot de passe avec logs détaillés
            log.info("Vérification du mot de passe...");
            log.info("Mot de passe fourni: {}", request.getPassword());
            log.info("Hash stocké: {}", admin.getPassword());

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), admin.getPassword());
            log.info("Résultat de la vérification: {}", passwordMatches);

            if (!passwordMatches) {
                log.warn("Mot de passe incorrect pour: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Credentials invalides", "Email ou mot de passe incorrect"));
            }

            // ✅ Génération du token
            log.info("Génération du token pour: {}", admin.getEmail());
            String token = jwtUtil.generateToken(admin.getEmail());
            log.info("Token généré avec succès");

            // ✅ Réponse structurée
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", admin.getEmail());
            response.put("message", "Connexion réussie");
            response.put("timestamp", System.currentTimeMillis());

            log.info("Connexion réussie pour: {}", admin.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'authentification pour {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur interne",
                            "Une erreur est survenue lors de l'authentification: " + e.getMessage()));
        }
    }

    // ✅ Endpoint pour valider un token
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");

            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Token requis", "Le token ne peut pas être vide"));
            }

            boolean isValid = jwtUtil.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);

            if (isValid) {
                String email = jwtUtil.extractEmail(token);
                response.put("email", email);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur validation", e.getMessage()));
        }
    }

    // ✅ Endpoint pour tester la connectivité
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        log.info("Test de connectivité appelé");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth endpoint accessible");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    // ✅ Endpoint pour débugger - À SUPPRIMER EN PRODUCTION
    @PostMapping("/debug")
    public ResponseEntity<?> debug(@RequestBody LoginRequest request) {
        try {
            Admin admin = adminService.findByEmail(request.getEmail());

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("emailFourni", request.getEmail());
            debugInfo.put("adminTrouve", admin != null);

            if (admin != null) {
                debugInfo.put("emailStocke", admin.getEmail());
                debugInfo.put("hashStocke", admin.getPassword());
                debugInfo.put("motDePasseFourni", request.getPassword());

                // Test de hash
                String testHash = passwordEncoder.encode(request.getPassword());
                debugInfo.put("nouveauHash", testHash);
                debugInfo.put("correspondance", passwordEncoder.matches(request.getPassword(), admin.getPassword()));
            }

            return ResponseEntity.ok(debugInfo);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erreur", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Crée une réponse d'erreur standardisée
     */
    private Map<String, Object> createErrorResponse(String message, String details) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("details", details);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}

@Data
class LoginRequest {
    private String email;
    private String password;
}