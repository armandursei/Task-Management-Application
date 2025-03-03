/**
 * Clasa de service pentru gestionarea logicii de afaceri legate de activități.
 * Intermediază între Controller și Repository.
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */


package com.backend.service;

import java.util.*;

import com.backend.entity.Activitati;
import com.backend.enums.StatusActivitate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.LucruDeFacutDTO;

import com.backend.repository.RepositoryActivitati;

@Service
public class ServiceActivitati {

    @Autowired
    private final RepositoryActivitati repositoryActivitati;

    @Autowired
    public ServiceActivitati(RepositoryActivitati repositoryActivitati) {
        this.repositoryActivitati = repositoryActivitati;
    }

    public List<Activitati> getActivitati() {
        return repositoryActivitati.findAll();
    }

    public void saveActivitate(Activitati activitate) {

//        List<Activitati> toateActivitatile = repositoryActivitati.findAll();
//
//        for (Activitati a : toateActivitatile) {
//            if (a.equals(activitate) && a.getIdActivitati() != activitate.getIdActivitati()) {
//                throw new IllegalArgumentException("O activitate similară există deja în baza de date.");
//            }
//        }

        if (activitate.getIdActivitati() > 0) {
            // Verifică dacă activitatea există
            Activitati activitateExistenta = repositoryActivitati.findById(activitate.getIdActivitati())
                    .orElseThrow(() -> new IllegalArgumentException("Activitatea cu acest ID nu există."));

            // Actualizează proprietățile existente
            activitateExistenta.setNume(activitate.getNume());
            activitateExistenta.setDescriere(activitate.getDescriere());
            activitateExistenta.setOraInceput(activitate.getOraInceput());
            activitateExistenta.setDurata(activitate.getDurata());

            // Salvează activitatea actualizată
            repositoryActivitati.save(activitateExistenta);
        } else {
            // Creează o activitate nouă
            repositoryActivitati.save(activitate);
        }
    }


    public void deleteActivitate(int id) {
        repositoryActivitati.deleteById(id);
    }


    public Activitati getActivitateById(int id) {
        Optional<Activitati> optional = repositoryActivitati.findById(id);
        return optional.orElse(null);
    }

    public List<String> validateActivitate(Activitati activitate) {
        List<String> errors = new ArrayList<>();

        if (activitate.getNume() == null || activitate.getNume().trim().isEmpty()) {
            errors.add("Numele nu poate fi gol.");
        }

        if (activitate.getDescriere() == null || activitate.getDescriere().trim().isEmpty()) {
            errors.add("Descrierea nu poate fi goală.");
        }

        if (activitate.getOraInceput() == null || !activitate.getOraInceput().matches("([01]?\\d|2[0-3]):([0-5]\\d)")) {
            errors.add("Ora trebuie să fie în formatul HH:MM (ex: 14:30) si HH intre 00 si 23, iar MM intre 00 si 60");
        }

        String durataStr = activitate.getDurata();
        if (durataStr == null || durataStr.trim().isEmpty()) {
            errors.add("Durata nu poate fi goală.");
        } else {
            for (int i = 0; i < durataStr.length(); i++) {
                char c = durataStr.charAt(i);
                if (c < '0' || c > '9') {
                    errors.add("Durata trebuie să conțină doar cifre.");
                    break;
                }
            }

            if (errors.isEmpty()) { // Validăm limitele doar dacă este numerică
                int durata = Integer.parseInt(durataStr);
                if (durata <= 0) {
                    errors.add("Durata trebuie să fie un număr pozitiv.");
                }
                if (durata > 600) {
                    errors.add("Durata nu poate depăși 600 minute.");
                }
            }
        }

        return errors;
    }

    public void removeAllDeFacut() {
        List<Activitati> activitatiDeFacut = repositoryActivitati.findByDeFacutTrue();
        for (Activitati activitate : activitatiDeFacut) {
            activitate.setDeFacut(false);
            activitate.setStatus(StatusActivitate.neinceputa); // Resetează statusul la "neinceputa"
        }
        repositoryActivitati.saveAll(activitatiDeFacut); // Salvează toate modificările
    }


    public void toggleDeFacut(int id) {
        Activitati activitate = repositoryActivitati.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activitatea cu ID-ul " + id + " nu există."));
        activitate.setDeFacut(!activitate.isDeFacut());
        repositoryActivitati.save(activitate);
    }

    public List<Activitati> getLucruriDeFacut() {
        return repositoryActivitati.findAll().stream()
                .filter(Activitati::isDeFacut)
                .sorted(Comparator.comparing(Activitati::getOraInceput))
                .toList();
    }

    public List<Activitati> getActivitatiDeFacut() {
        return repositoryActivitati.findByDeFacutTrue();
    }

    public List<LucruDeFacutDTO> getLucruriDeFacutWithBreaks() {
        List<Activitati> activitati = getLucruriDeFacut(); // Activități sortate după ora de început
        List<LucruDeFacutDTO> listaCuPauze = new ArrayList<>();

        for (int i = 0; i < activitati.size(); i++) {
            Activitati curenta = activitati.get(i);

            // Adăugăm activitatea curentă
            listaCuPauze.add(new LucruDeFacutDTO(
                    "activitate",
                    curenta.getIdActivitati(),
                    curenta.getNume(),
                    curenta.getDescriere(),
                    curenta.getOraInceput(),
                    curenta.getDurata(),
                    curenta.isDeFacut(), // Setăm valoarea deFacut
                    curenta.getStatus(), // Atribuire status
                    curenta.getProgres()
            ));

            if (i < activitati.size() - 1) {
                Activitati urmatoarea = activitati.get(i + 1);

                // Calculăm ora de sfârșit a activității curente
                String oraSfarsitCurenta = calculeazaOraSfarsit(curenta.getOraInceput(), curenta.getDurata());

                // Calculăm durata pauzei
                int durataPauza = calculeazaDurataPauzei(oraSfarsitCurenta, urmatoarea.getOraInceput());
                if (durataPauza > 0) {
                    listaCuPauze.add(new LucruDeFacutDTO(
                            "pauza",

                            oraSfarsitCurenta,
                            String.valueOf(durataPauza)
                    ));
                }
            }
        }

        return listaCuPauze;
    }

    private int calculeazaDurataPauzei(String oraSfarsit, String oraInceputUrmatoare) {
        try {
            String[] sfarsitParts = oraSfarsit.split(":");
            String[] inceputParts = oraInceputUrmatoare.split(":");

            int oraSffarsit = Integer.parseInt(sfarsitParts[0]);
            int minutSfarsit = Integer.parseInt(sfarsitParts[1]);
            int oraInceput = Integer.parseInt(inceputParts[0]);
            int minutInceput = Integer.parseInt(inceputParts[1]);

            // Calculăm minutele totale pentru ambele ore
            int totalSfarsit = oraSffarsit * 60 + minutSfarsit;
            int totalInceput = oraInceput * 60 + minutInceput;

            return Math.max(totalInceput - totalSfarsit, 0); // Pauza este diferența dintre minute
        } catch (Exception e) {
            throw new IllegalArgumentException("Formatul orei este invalid.");
        }
    }


    private String calculeazaOraSfarsit(String oraInceput, String durata) {
        try {
            // Ora în format HH:mm
            String[] parts = oraInceput.split(":");
            int ora = Integer.parseInt(parts[0]);
            int minut = Integer.parseInt(parts[1]);
            int durataMinute = Integer.parseInt(durata);

            // Adăugăm durata în minute
            minut += durataMinute;
            ora += minut / 60; // Convertim minutele în ore
            minut %= 60;

            // Returnăm ora calculată în format HH:mm
            return String.format("%02d:%02d", ora, minut);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formatul orei sau duratei este invalid.");
        }
    }

    private boolean isPauzaNecesara(String oraSfarsit, String oraInceputUrmatoare) {
        try {
            String[] sfarsitParts = oraSfarsit.split(":");
            String[] inceputParts = oraInceputUrmatoare.split(":");

            int oraSffarsit = Integer.parseInt(sfarsitParts[0]);
            int minutSfarsit = Integer.parseInt(sfarsitParts[1]);
            int oraInceput = Integer.parseInt(inceputParts[0]);
            int minutInceput = Integer.parseInt(inceputParts[1]);

            // Calculăm minutele totale pentru ambele ore
            int totalSfarsit = oraSffarsit * 60 + minutSfarsit;
            int totalInceput = oraInceput * 60 + minutInceput;

            return totalInceput > totalSfarsit; // Pauză necesară dacă există un interval între sfârșit și început
        } catch (Exception e) {
            throw new IllegalArgumentException("Formatul orei este invalid.");
        }
    }

    public List<Activitati> searchActivitati(String query) {
        if (query == null || query.isEmpty()) {
            return repositoryActivitati.findAll(); // Dacă nu există query, returnăm toate activitățile
        }
        return repositoryActivitati.findByNumeContainingOrDescriereContaining(query, query);
    }


    public double calculateProgress() {
        List<Activitati> activitatiDeFacut = repositoryActivitati.findByDeFacutTrue();

        if (activitatiDeFacut.isEmpty()) {
            System.out.println("Lista activităților de făcut este goală.");
        } else {
            System.out.println("Activitățile de făcut:");
            for (Activitati activitate : activitatiDeFacut) {
                System.out.println("Nume: " + activitate.getNume() + ", Durată: " + activitate.getDurata() + ", Status: " + activitate.getStatus());
            }
        }

        int totalDurata = 0;
        int progresDurata = 0;

        for (Activitati activitate : activitatiDeFacut) {
            if (activitate.getStatus() != null) {
                totalDurata += Integer.parseInt(activitate.getDurata()); // Adaugă durata totală

                if (activitate.getStatus() == StatusActivitate.in_progres) {
                    progresDurata += Integer.parseInt(activitate.getDurata()) * Integer.parseInt(activitate.getProgres()) /100 ;
                } else if (activitate.getStatus() == StatusActivitate.finalizata) {
                    progresDurata += Integer.parseInt(activitate.getDurata()); // Durata completă
                }
            }
        }

        // Evităm împărțirea la zero
//        System.out.println("Progresul calculat este: " + progresDurata / totalDurata);

        return totalDurata <= 0.1 ? 0 : (double) progresDurata / totalDurata;
    }

    public boolean toateActivitatileAnterioareSuntFinalizate(Activitati activitateCurenta) {
        int oraInceputCurenta = parseOraInMinutes(activitateCurenta.getOraInceput());
        List<Activitati> activitatiDeFacut = repositoryActivitati.findByDeFacutTrue();

        for (Activitati activitate : activitatiDeFacut) {
            int oraInceput = parseOraInMinutes(activitate.getOraInceput());
            if (oraInceput < oraInceputCurenta && !activitate.getStatus().equals(StatusActivitate.finalizata)) {
                return false; // Găsită activitate care începe înainte și nu e finalizată
            }
        }
        return true; // Toate activitățile anterioare sunt finalizate
    }

    private int parseOraInMinutes(String ora) {
        try {
            String[] parts = ora.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes; // Convertim totul în minute
        } catch (Exception e) {
            throw new IllegalArgumentException("Ora nu este validă: " + ora);
        }
    }


}
