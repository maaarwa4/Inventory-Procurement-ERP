package com.mon_projet_pfa.backend.controllers;

import com.mon_projet_pfa.backend.models.Admin;
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
@RequestMapping("/api/profile")
@CrossOrigin(origins = { "http://localhost:4200", "http://127.0.0.1:4200" })
public class ProfileController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("Demande de changement de mot de passe pour: {}", request.getEmail());

        try {
            // Validation des données
            if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Mot de passe actuel requis"));
            }

            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Le nouveau mot de passe doit contenir au moins 6 caractères"));
            }

            // Vérifier l'admin existe
            Admin admin = adminService.findByEmail(request.getEmail());
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Utilisateur non trouvé"));
            }

            // Vérifier le mot de passe actuel
            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Mot de passe actuel incorrect"));
            }

            // Changer le mot de passe
            admin.setPassword(passwordEncoder.encode(request.getNewPassword()));

            // ✅ Sauvegarder l'objet Admin complet
            adminService.saveAdmin(admin); // ou adminService.update(admin)

            log.info("Mot de passe changé avec succès pour: {}", request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Mot de passe modifié avec succès");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors du changement de mot de passe: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur interne du serveur"));
        }
    }

    @PostMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailRequest request) {
        log.info("Demande de modification email: {} -> {}", request.getCurrentEmail(), request.getNewEmail());

        try {
            // Validation
            if (request.getNewEmail() == null || !request.getNewEmail().contains("@")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email invalide"));
            }

            if (request.getCurrentEmail() == null || request.getCurrentEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email actuel requis"));
            }

            // Vérifier que le nouvel email n'existe pas déjà
            Admin existingAdmin = adminService.findByEmail(request.getNewEmail());
            if (existingAdmin != null && !existingAdmin.getEmail().equals(request.getCurrentEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(createErrorResponse("Cet email est déjà utilisé"));
            }

            // Vérifier l'admin actuel
            Admin admin = adminService.findByEmail(request.getCurrentEmail());
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Utilisateur non trouvé"));
            }

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Mot de passe incorrect"));
            }

            // Mettre à jour l'email
            admin.setEmail(request.getNewEmail().trim().toLowerCase());

            // ✅ Sauvegarder l'objet Admin complet
            adminService.saveAdmin(admin); // ou adminService.update(admin)

            log.info("Email modifié avec succès: {} -> {}", request.getCurrentEmail(), admin.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Email modifié avec succès");
            response.put("newEmail", admin.getEmail());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la modification de l'email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur interne du serveur"));
        }
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest request) {
        log.info("Demande de suppression de compte pour: {}", request.getEmail());

        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email requis"));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Mot de passe requis"));
            }

            Admin admin = adminService.findByEmail(request.getEmail());
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Utilisateur non trouvé"));
            }

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Mot de passe incorrect"));
            }

            // Supprimer le compte
            adminService.deleteById(admin.getId());

            log.info("Compte supprimé pour: {}", request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Compte supprimé avec succès");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la suppression: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur interne du serveur"));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}

// DTOs
@Data
class ChangePasswordRequest {
    private String email;
    private String currentPassword;
    private String newPassword;
}

@Data
class UpdateEmailRequest {
    private String currentEmail;
    private String newEmail;
    private String currentPassword;
}

@Data
class DeleteAccountRequest {
    private String email;
    private String password;
}