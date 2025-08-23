package com.mon_projet_pfa.backend.repositories;

import com.mon_projet_pfa.backend.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Utilisez firstName au lieu de name
    List<Supplier> findByFirstNameContainingIgnoreCase(String firstName);

    List<Supplier> findByCity(String city);

    List<Supplier> findByCountry(String country);

    List<Supplier> findByIsActive(Boolean isActive);

    // Modifié pour utiliser firstName
    List<Supplier> findByFirstNameContainingIgnoreCaseAndCity(String firstName, String city);

    // Custom search query (correct)
    @Query("SELECT s FROM Supplier s WHERE " +
            "(LOWER(s.firstName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(s.companyName) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    List<Supplier> searchSuppliers(@Param("keyword") String keyword);

    // Deactivate (correct)
    @Modifying
    @Query("UPDATE Supplier s SET s.isActive = false WHERE s.id = :id")
    void deactivateById(@Param("id") Long id);

    // ➜ Ajoute l’activation symétrique
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Supplier s set s.isActive = true where s.id = :id")
    void activateById(@Param("id") Long id);
}