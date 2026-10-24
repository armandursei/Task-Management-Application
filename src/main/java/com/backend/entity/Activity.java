/**
 * JPA entity for an activity managed by the application.
 * Column names match the existing {@code ACTIVITATI} table.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */


package com.backend.entity;

import jakarta.persistence.*;

import com.backend.enums.ActivityStatus;
import com.backend.enums.ActivityStatusConverter;

import java.util.Objects;

@Entity
@Table(name = "ACTIVITATI")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Activitati")
    private int id;

    @Column(name = "Nume")
    private String name;

    @Column(name = "Descriere")
    private String description;

    /** Start time in HH:MM format. */
    @Column(name = "Ora_Inceput")
    private String startTime;

    /** Duration in minutes. */
    @Column(name = "Durata")
    private String duration;

    /** Whether the activity has been added to the to-do list. */
    @Column(name = "de_facut")
    private boolean inTodoList;

    @Convert(converter = ActivityStatusConverter.class)
    @Column(name = "status")
    private ActivityStatus status = ActivityStatus.NOT_STARTED;

    /** Completion percentage (0-100). */
    @Column(name = "progres")
    private String progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isInTodoList() {
        return inTodoList;
    }

    public void setInTodoList(boolean inTodoList) {
        this.inTodoList = inTodoList;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    /** Two activities are equal when name, description, start time and duration match. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity that = (Activity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, startTime, duration);
    }

}
