/**
 * Clasa entitate care reprezintă activitățile gestionate în aplicație.
 * Aceasta este mapă la tabela din baza de date.
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */


package com.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import com.backend.enums.StatusActivitate;

import java.util.Objects;

@Entity
@Table(name = "ACTIVITATI")
public class Activitati {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Activitati")
    private int idActivitati; // Schimbat în camelCase

    @Column(name = "Nume")
    private String nume;

    @Column(name = "Descriere")
    private String descriere;

    @Column(name = "Ora_Inceput")
    private String oraInceput;

    @Column(name = "Durata")
    private String durata;

    // Getter și Setter pentru idActivitati
    public int getIdActivitati() {
        return idActivitati;
    }

    public void setIdActivitati(int idActivitati) {
        this.idActivitati = idActivitati;
    }

    // Getter și Setter pentru nume
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    // Getter și Setter pentru descriere
    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    // Getter și Setter pentru oraInceput
    public String getOraInceput() {
        return oraInceput;
    }

    public void setOraInceput(String oraInceput) {
        this.oraInceput = oraInceput;
    }

    // Getter și Setter pentru durata
    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    @Column(name = "de_facut")
    private boolean deFacut; // Indică dacă activitatea este marcată pentru "Lucruri de făcut"

    // Getteri și setteri
    public boolean isDeFacut() {
        return deFacut;
    }

    public void setDeFacut(boolean deFacut) {
        this.deFacut = deFacut;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusActivitate status = StatusActivitate.neinceputa;

    public StatusActivitate getStatus() {
        return status;
    }

    public void setStatus(StatusActivitate status) {
        this.status = status;
    }

    @Column(name = "progres")
    private String progres;

    // Getter și Setter pentru durata
    public String getProgres() {
        return progres;
    }

    public void setProgres(String progres) {
        this.progres = progres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activitati that = (Activitati) o;
        return Objects.equals(nume, that.nume) &&
                Objects.equals(descriere, that.descriere) &&
                Objects.equals(oraInceput, that.oraInceput) &&
                Objects.equals(durata, that.durata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume, descriere, oraInceput, durata);
    }

}
