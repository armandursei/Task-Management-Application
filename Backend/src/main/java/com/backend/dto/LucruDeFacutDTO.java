/**
 * Clasa DTO pentru transferul de date legate de activitățile care trebuie efectuate.
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */

package com.backend.dto;

import com.backend.enums.StatusActivitate;

public class LucruDeFacutDTO {
    private String type; // "activitate" sau "pauza"
    private Integer id; // ID-ul activității, nul pentru pauze
    private String nume; // Pentru activități
    private String descriere; // Pentru activități
    private String oraInceput;
    private String durata;
    private boolean deFacut; // Indicator pentru „De făcut”
    private StatusActivitate status; // Enum pentru status
    private String progres;

    // Constructor pentru activități
    public LucruDeFacutDTO(String type, Integer id, String nume, String descriere, String oraInceput, String durata, boolean deFacut, StatusActivitate status, String progres) {
        this.type = type;
        this.id = id;
        this.nume = nume;
        this.descriere = descriere;
        this.oraInceput = oraInceput;
        this.durata = durata;
        this.deFacut = deFacut;
        this.status = status;
        this.progres = progres;
    }

    // Constructor pentru pauze
    public LucruDeFacutDTO(String type, String oraInceput, String durata) {
        this.type = type;
        this.id = null; // Pauzele nu au ID
        this.nume = null;
        this.descriere = null;
        this.oraInceput = oraInceput;
        this.durata = durata;
        this.deFacut = false; // Pauzele nu sunt „De făcut”
    }

    // Getteri și setteri
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getOraInceput() {
        return oraInceput;
    }

    public void setOraInceput(String oraInceput) {
        this.oraInceput = oraInceput;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public boolean isDeFacut() {
        return deFacut;
    }

    public void setDeFacut(boolean deFacut) {
        this.deFacut = deFacut;
    }

    public StatusActivitate getStatus() { // Getter pentru status
        return status;
    }

    public void setStatus(StatusActivitate status) { // Setter pentru status
        this.status = status;
    }

    // Getter și Setter pentru durata
    public String getProgres() {
        return progres;
    }

    public void setProgres(String progres) {
        this.progres = progres;
    }
}
