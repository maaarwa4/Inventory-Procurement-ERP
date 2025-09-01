package com.mon_projet_pfa.backend.services;

import com.mon_projet_pfa.backend.models.Admin;
import com.mon_projet_pfa.backend.repositories.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin saveAdmin(Admin admin) {
        try {
            log.info("Sauvegarde admin: {}", admin.getEmail());

            // Normalisation email
            admin.setEmail(admin.getEmail().trim().toLowerCase());

            // Si le mot de passe n’est pas déjà encodé, on l’encode
            if (!admin.getPassword().startsWith("$2a$")) { // Vérifie si déjà BCrypt
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }

            Admin savedAdmin = adminRepository.save(admin);
            log.info("Admin sauvegardé avec succès: {}", savedAdmin.getEmail());

            return savedAdmin;
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde de l'admin: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible de sauvegarder l'admin", e);
        }
    }

    public Admin findByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                log.warn("Email vide fourni à findByEmail");
                return null;
            }

            String normalizedEmail = email.trim().toLowerCase();
            log.info("Recherche admin avec email: {}", normalizedEmail);

            Admin admin = adminRepository.findByEmail(normalizedEmail).orElse(null);

            if (admin != null) {
                log.info("Admin trouvé: {}", admin.getEmail());
            } else {
                log.warn("Aucun admin trouvé pour email: {}", normalizedEmail);
            }

            return admin;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'admin: {}", e.getMessage(), e);
            return null;
        }
    }

    // ✅ Méthode pour vérifier les credentials
    public boolean verifyCredentials(String email, String rawPassword) {
        try {
            Admin admin = findByEmail(email);
            if (admin == null) {
                return false;
            }

            boolean matches = passwordEncoder.matches(rawPassword, admin.getPassword());
            log.info("Vérification du mot de passe pour {}: {}", email, matches);

            return matches;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification des credentials: {}", e.getMessage(), e);
            return false;
        }
    }

    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }
}