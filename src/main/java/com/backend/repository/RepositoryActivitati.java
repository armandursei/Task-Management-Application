/**
 * Repository pentru gestionarea operațiunilor CRUD asupra entității Activitati.
 * Folosește Spring Data JPA pentru interacțiunea cu baza de date.
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */


package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.entity.Activitati;

import java.util.List;


public interface RepositoryActivitati extends JpaRepository<Activitati, Integer> {
    List<Activitati> findByNumeContainingOrDescriereContaining(String nume, String descriere);
    List<Activitati> findByDeFacutTrue();

}
