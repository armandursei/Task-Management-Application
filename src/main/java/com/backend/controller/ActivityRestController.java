/**
 * REST controller exposing CRUD operations on {@link Activity}:
 * create, read, update, delete and list.
 * @author Ursei George-Armand
 * @version 10 December 2024
 */

package com.backend.controller;

import com.backend.entity.Activity;
import com.backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;

    /**
     * Returns all activities.
     * @return the list of activities.
     */
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getActivities();
        return ResponseEntity.ok(activities);
    }

    /**
     * Creates a new activity.
     * @param activity the activity to create.
     * @return the created activity.
     */
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        activityService.saveActivity(activity);
        return new ResponseEntity<>(activity, HttpStatus.CREATED);
    }

    /**
     * Returns an activity by ID.
     * @param id the activity ID.
     * @return the activity, or 404 if it does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable int id) {
        Activity activity = activityService.getActivityById(id);
        if (activity != null) {
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Updates an existing activity.
     * @param id the ID of the activity to update.
     * @param activity the updated activity.
     * @return the updated activity, or 404 if it does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable int id, @RequestBody Activity activity) {
        Activity existingActivity = activityService.getActivityById(id);
        if (existingActivity != null) {
            activity.setId(id);
            activityService.saveActivity(activity);
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes an activity by ID.
     * @param id the ID of the activity to delete.
     * @return 204 on success, or 404 if it does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable int id) {
        Activity activity = activityService.getActivityById(id);
        if (activity != null) {
            activityService.deleteActivity(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
