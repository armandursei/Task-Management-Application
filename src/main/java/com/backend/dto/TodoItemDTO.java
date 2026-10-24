/**
 * DTO for one row of the to-do list: either an activity or a break
 * between two consecutive activities.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */

package com.backend.dto;

import com.backend.enums.ActivityStatus;

public class TodoItemDTO {
    private String type; // "activity" or "break"
    private Integer id; // activity ID, null for breaks
    private String name;
    private String description;
    private String startTime;
    private String duration;
    private boolean inTodoList;
    private ActivityStatus status;
    private String progress;

    /** Creates an activity row. */
    public TodoItemDTO(String type, Integer id, String name, String description, String startTime, String duration, boolean inTodoList, ActivityStatus status, String progress) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.inTodoList = inTodoList;
        this.status = status;
        this.progress = progress;
    }

    /** Creates a break row. */
    public TodoItemDTO(String type, String startTime, String duration) {
        this.type = type;
        this.id = null;
        this.name = null;
        this.description = null;
        this.startTime = startTime;
        this.duration = duration;
        this.inTodoList = false;
    }

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
}
