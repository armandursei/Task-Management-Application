/**
 * Controler REST pentru gestionarea operațiunilor CRUD asupra entității Activitati.
 * Oferă funcționalități precum creare, actualizare, ștergere și listare a activităților.
 * @author Ursei George-Armand
 * @version 10 Decembrie 2024
 */

package com.backend.controller;

import com.backend.entity.Activitati;
import com.backend.service.ServiceActivitati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activitati")
public class ActivitatiRestController {

    @Autowired
    private ServiceActivitati serviceActivitati;

    /**
     * Obține toate activitățile.
     * @return o listă de activități.
     */
    @GetMapping
    public ResponseEntity<List<Activitati>> getAllActivitati() {
        List<Activitati> activitati = serviceActivitati.getActivitati();
        return ResponseEntity.ok(activitati);
    }

    /**
     * Creează o activitate nouă.
     * @param activitate entitatea de creat.
     * @return activitatea creată.
     */
    @PostMapping
    public ResponseEntity<Activitati> createActivitate(@RequestBody Activitati activitate) {
        serviceActivitati.saveActivitate(activitate);
        return new ResponseEntity<>(activitate, HttpStatus.CREATED);
    }

    /**
     * Obține o activitate după ID.
     * @param id ID-ul activității.
     * @return activitatea găsită sau 404 dacă nu există.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activitati> getActivitateById(@PathVariable int id) {
        Activitati activitate = serviceActivitati.getActivitateById(id);
        if (activitate != null) {
            return ResponseEntity.ok(activitate);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Actualizează o activitate existentă.
     * @param id ID-ul activității de actualizat.
     * @param activitate entitatea actualizată.
     * @return activitatea actualizată sau 404 dacă nu există.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Activitati> updateActivitate(
            @PathVariable int id, @RequestBody Activitati activitate) {
        Activitati existingActivitate = serviceActivitati.getActivitateById(id);
        if (existingActivitate != null) {
            activitate.setIdActivitati(id);
            serviceActivitati.saveActivitate(activitate);
            return ResponseEntity.ok(activitate);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Șterge o activitate după ID.
     * @param id ID-ul activității de șters.
     * @return răspuns de succes sau 404 dacă nu există.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivitate(@PathVariable int id) {
        Activitati activitate = serviceActivitati.getActivitateById(id);
        if (activitate != null) {
            serviceActivitati.deleteActivitate(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
