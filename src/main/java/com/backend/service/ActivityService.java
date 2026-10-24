/**
 * Business logic for activities: CRUD, validation, the to-do list schedule
 * (with breaks between activities) and overall progress calculation.
 * Sits between the controllers and the repository.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */


package com.backend.service;

import java.util.*;

import com.backend.entity.Activity;
import com.backend.enums.ActivityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.TodoItemDTO;

import com.backend.repository.ActivityRepository;

@Service
public class ActivityService {

    @Autowired
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getActivities() {
        return activityRepository.findAll();
    }

    /**
     * Creates a new activity, or updates name, description, start time and
     * duration of an existing one when the ID is set.
     */
    public void saveActivity(Activity activity) {
        if (activity.getId() > 0) {
            Activity existingActivity = activityRepository.findById(activity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No activity exists with this ID."));

            existingActivity.setName(activity.getName());
            existingActivity.setDescription(activity.getDescription());
            existingActivity.setStartTime(activity.getStartTime());
            existingActivity.setDuration(activity.getDuration());

            activityRepository.save(existingActivity);
        } else {
            activityRepository.save(activity);
        }
    }


    public void deleteActivity(int id) {
        activityRepository.deleteById(id);
    }


    public Activity getActivityById(int id) {
        Optional<Activity> optional = activityRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * Validates the user-editable fields of an activity.
     * @return a list of error messages; empty when the activity is valid.
     */
    public List<String> validateActivity(Activity activity) {
        List<String> errors = new ArrayList<>();

        if (activity.getName() == null || activity.getName().trim().isEmpty()) {
            errors.add("Name cannot be empty.");
        }

        if (activity.getDescription() == null || activity.getDescription().trim().isEmpty()) {
            errors.add("Description cannot be empty.");
        }

        if (activity.getStartTime() == null || !activity.getStartTime().matches("([01]?\\d|2[0-3]):([0-5]\\d)")) {
            errors.add("Start time must be in HH:MM format (e.g. 14:30), with HH between 00 and 23 and MM between 00 and 59.");
        }

        String durationStr = activity.getDuration();
        if (durationStr == null || durationStr.trim().isEmpty()) {
            errors.add("Duration cannot be empty.");
        } else {
            for (int i = 0; i < durationStr.length(); i++) {
                char c = durationStr.charAt(i);
                if (c < '0' || c > '9') {
                    errors.add("Duration must contain digits only.");
                    break;
                }
            }

            if (errors.isEmpty()) { // only check the range when the value is numeric
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0) {
                    errors.add("Duration must be a positive number.");
                }
                if (duration > 600) {
                    errors.add("Duration cannot exceed 600 minutes.");
                }
            }
        }

        return errors;
    }

    /** Removes every activity from the to-do list and resets its status. */
    public void removeAllFromTodoList() {
        List<Activity> todoActivities = activityRepository.findByInTodoListTrue();
        for (Activity activity : todoActivities) {
            activity.setInTodoList(false);
            activity.setStatus(ActivityStatus.NOT_STARTED);
        }
        activityRepository.saveAll(todoActivities);
    }


    public void toggleTodo(int id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No activity exists with ID " + id + "."));
        activity.setInTodoList(!activity.isInTodoList());
        activityRepository.save(activity);
    }

    /** To-do list activities sorted by start time. */
    public List<Activity> getSortedTodoActivities() {
        return activityRepository.findAll().stream()
                .filter(Activity::isInTodoList)
                .sorted(Comparator.comparing(Activity::getStartTime))
                .toList();
    }

    public List<Activity> getTodoActivities() {
        return activityRepository.findByInTodoListTrue();
    }

    /**
     * Builds the to-do schedule: activities sorted by start time, with a
     * "break" row inserted wherever there is free time between two activities.
     */
    public List<TodoItemDTO> getTodoItemsWithBreaks() {
        List<Activity> activities = getSortedTodoActivities();
        List<TodoItemDTO> itemsWithBreaks = new ArrayList<>();

        for (int i = 0; i < activities.size(); i++) {
            Activity current = activities.get(i);

            itemsWithBreaks.add(new TodoItemDTO(
                    "activity",
                    current.getId(),
                    current.getName(),
                    current.getDescription(),
                    current.getStartTime(),
                    current.getDuration(),
                    current.isInTodoList(),
                    current.getStatus(),
                    current.getProgress()
            ));

            if (i < activities.size() - 1) {
                Activity next = activities.get(i + 1);

                String currentEndTime = calculateEndTime(current.getStartTime(), current.getDuration());

                int breakDuration = calculateBreakDuration(currentEndTime, next.getStartTime());
                if (breakDuration > 0) {
                    itemsWithBreaks.add(new TodoItemDTO(
                            "break",
                            currentEndTime,
                            String.valueOf(breakDuration)
                    ));
                }
            }
        }

        return itemsWithBreaks;
    }

    /** Minutes between the end of one activity and the start of the next (never negative). */
    private int calculateBreakDuration(String endTime, String nextStartTime) {
        try {
            String[] endParts = endTime.split(":");
            String[] startParts = nextStartTime.split(":");

            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            int totalEnd = endHour * 60 + endMinute;
            int totalStart = startHour * 60 + startMinute;

            return Math.max(totalStart - totalEnd, 0);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format.");
        }
    }


    /** Adds a duration in minutes to an HH:mm start time and returns the end time as HH:mm. */
    private String calculateEndTime(String startTime, String duration) {
        try {
            String[] parts = startTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int durationMinutes = Integer.parseInt(duration);

            minute += durationMinutes;
            hour += minute / 60;
            minute %= 60;

            return String.format("%02d:%02d", hour, minute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time or duration format.");
        }
    }

    public List<Activity> searchActivities(String query) {
        if (query == null || query.isEmpty()) {
            return activityRepository.findAll();
        }
        return activityRepository.findByNameContainingOrDescriptionContaining(query, query);
    }


    /**
     * Overall progress of the to-do list, weighted by duration:
     * completed activities count fully, in-progress ones by their percentage.
     * @return a value between 0 and 1.
     */
    public double calculateProgress() {
        List<Activity> todoActivities = activityRepository.findByInTodoListTrue();

        int totalDuration = 0;
        int completedDuration = 0;

        for (Activity activity : todoActivities) {
            if (activity.getStatus() != null) {
                totalDuration += Integer.parseInt(activity.getDuration());

                if (activity.getStatus() == ActivityStatus.IN_PROGRESS) {
                    completedDuration += Integer.parseInt(activity.getDuration()) * Integer.parseInt(activity.getProgress()) /100 ;
                } else if (activity.getStatus() == ActivityStatus.COMPLETED) {
                    completedDuration += Integer.parseInt(activity.getDuration());
                }
            }
        }

        return totalDuration <= 0.1 ? 0 : (double) completedDuration / totalDuration;
    }

    /** True when every to-do activity that starts earlier than the given one is completed. */
    public boolean areAllPreviousActivitiesCompleted(Activity currentActivity) {
        int currentStart = parseTimeToMinutes(currentActivity.getStartTime());
        List<Activity> todoActivities = activityRepository.findByInTodoListTrue();

        for (Activity activity : todoActivities) {
            int start = parseTimeToMinutes(activity.getStartTime());
            if (start < currentStart && !activity.getStatus().equals(ActivityStatus.COMPLETED)) {
                return false;
            }
        }
        return true;
    }

    private int parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time: " + time);
        }
    }


}
