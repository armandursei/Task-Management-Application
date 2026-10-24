/**
 * MVC controller for the Thymeleaf UI: handles page requests and form
 * submissions (activity list, to-do list, search, add/edit, status and
 * progress updates) and the confirmation/error overlays.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */


package com.backend.controller;

import com.backend.entity.Activity;
import com.backend.enums.ActivityStatus;
import com.backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ActivityController {

    private static final String ADD_TO_TODO_TEXT = "Add to To-Do";
    private static final String REMOVE_TEXT = "Remove";
    private static final String HIDE_LIST_TEXT = "Hide List";
    private static final String SHOW_LIST_TEXT = "Show Activities";

    @Autowired
    private ActivityService activityService;
    private double progress;
    private boolean showSuccessOverlay = false;
    private int showList = 0; // 1 when the activity list is visible
    private String listButtonText;
    /** Activity ID -> label of its to-do toggle button. */
    private Map<Long, String> todoButtonText;
    private boolean showDeleteAllConfirmation = false;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("showList", showList);

        if (showList == 1) {
            model.addAttribute("activities", activityService.getActivities());
        }
        listButtonText = showList == 1 ? HIDE_LIST_TEXT : SHOW_LIST_TEXT;


        model.addAttribute("todoItems", activityService.getTodoItemsWithBreaks());
        model.addAttribute("statusOptions", ActivityStatus.values());
        progress = activityService.calculateProgress();
        model.addAttribute("progress", Math.round(progress * 10000.0) / 100.0);
        model.addAttribute("listButtonText", listButtonText);


        List<Activity> activities = activityService.getActivities();

        todoButtonText = new HashMap<>();
        for (Activity activity : activities) {
            todoButtonText.put((long) activity.getId(), activity.isInTodoList() ? REMOVE_TEXT : ADD_TO_TODO_TEXT);
        }

        model.addAttribute("activities", activities);
        model.addAttribute("todoButtonText", todoButtonText);


        model.addAttribute("showDeleteConfirmation", false);
        model.addAttribute("deleteId", null);

        model.addAttribute("showSuccessOverlay", showSuccessOverlay);
        showSuccessOverlay = false; // show the success message only once

        return "index";
    }

    @PostMapping("/toggleList")
    public String toggleList() {
        showList = (showList + 1) % 2;
        return "redirect:/";
    }



    @GetMapping("/search")
    public String searchActivities(@RequestParam("query") String query, Model model) {
        List<Activity> results = activityService.searchActivities(query);
        model.addAttribute("activities", results);
        model.addAttribute("todoItems", activityService.getTodoItemsWithBreaks());

        listButtonText = showList == 1 ? HIDE_LIST_TEXT : SHOW_LIST_TEXT;

        model.addAttribute("progress", Math.round(progress * 10000.0) / 100.0);

        model.addAttribute("todoButtonText", todoButtonText);

        model.addAttribute("query", query);
        model.addAttribute("showList", 1); // always show the list with search results
        if (showList == 0) {
            showList = (showList + 1) % 2;
            listButtonText = showList == 1 ? HIDE_LIST_TEXT : SHOW_LIST_TEXT;

        }
        model.addAttribute("listButtonText", listButtonText);

        if (results.isEmpty()) {
            model.addAttribute("noResults", true);
            model.addAttribute("errorMessage", "No activity matches the search text.");

        } else {
            model.addAttribute("noResults", false);
        }

        return "index";
    }



    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("showForm", true);
        model.addAttribute("formTitle", "Add Activity");
        model.addAttribute("formAction", "/add");
        model.addAttribute("activity", new Activity());
        return "index";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam int id, Model model) {
        Activity activity = activityService.getActivityById(id);
        model.addAttribute("showForm", true);
        model.addAttribute("formTitle", "Edit Activity");
        model.addAttribute("formAction", "/update");
        model.addAttribute("activity", activity);
        return "index";
    }


    @PostMapping("/add")
    public String addActivity(@ModelAttribute Activity activity, Model model) {
        List<String> errors = activityService.validateActivity(activity);

        // Reject duplicates (same name, description, start time and duration)
        List<Activity> allActivities = activityService.getActivities();
        for (Activity a : allActivities) {
            if (a.equals(activity) && a.getId() != activity.getId()) {
                errors.add("A similar activity already exists in the database.");
                break;
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("showForm", true);
            model.addAttribute("activity", activity); // keep the submitted values
            model.addAttribute("formTitle", "Add Activity");
            model.addAttribute("formAction", "/add");
            model.addAttribute("showList", 0);

            return "index";
        }

        activityService.saveActivity(activity);

        showSuccessOverlay = true;
        model.addAttribute("showSuccessOverlay", showSuccessOverlay);

        return "redirect:/";
    }




    @PostMapping("/update")
    public String updateActivity(@ModelAttribute Activity activity, Model model) {
        List<String> errors = activityService.validateActivity(activity);

        // Reject duplicates (same name, description, start time and duration)
        List<Activity> allActivities = activityService.getActivities();
        for (Activity a : allActivities) {
            if (a.equals(activity) && a.getId() != activity.getId()) {
                errors.add("A similar activity already exists in the database.");
                break;
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("showForm", true);
            model.addAttribute("activity", activity);
            model.addAttribute("formTitle", "Edit Activity");
            model.addAttribute("formAction", "/update");
            model.addAttribute("showList", 0);

            return "index";
        }

        Activity existingActivity = activityService.getActivityById(activity.getId());
        if (existingActivity == null) {
            throw new IllegalArgumentException("No activity exists with this ID.");
        }
        existingActivity.setName(activity.getName());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartTime(activity.getStartTime());
        existingActivity.setDuration(activity.getDuration());

        activityService.saveActivity(existingActivity);

        showSuccessOverlay = true;
        model.addAttribute("showSuccessOverlay", showSuccessOverlay);

        return "redirect:/";
    }

    @GetMapping("/confirmDelete")
    public String confirmDelete(@RequestParam("id") Long id, Model model) {
        model.addAttribute("showDeleteConfirmation", true);
        model.addAttribute("deleteId", id);
        return "index";
    }

    @PostMapping("/showDeleteConfirmation")
    public String showDeleteConfirmation(@RequestParam("id") Long id, Model model) {
        model.addAttribute("showDeleteConfirmation", true);
        model.addAttribute("deleteId", id);
        return "index";
    }



    @PostMapping("/delete")
    public String deleteActivity(@RequestParam("id") int id) {
        activityService.deleteActivity(id);
        return "redirect:/";
    }


    /**
     * Adds an activity to the to-do list or removes it from the list.
     * Adding is rejected when the activity overlaps one already in the list.
     */
    @PostMapping("/toggleTodo")
    public String toggleTodo(@RequestParam("id") int id, Model model) {
        try {
            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                throw new IllegalArgumentException("No activity exists with this ID.");
            }

            if (!activity.isInTodoList()) {
                List<Activity> todoActivities = activityService.getTodoActivities();

                for (Activity a : todoActivities) {
                    if (overlaps(activity, a)) {
                        model.addAttribute("errorMessage", "This activity overlaps with one already in the to-do list.");
                        model.addAttribute("showErrorOverlay", true);
                        return "index";
                    }
                }
            }

            activity.setInTodoList(!activity.isInTodoList());
            activity.setStatus(ActivityStatus.NOT_STARTED);
            activity.setProgress("0");
            activityService.saveActivity(activity);

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/confirmRemoveAll")
    public String confirmRemoveAll(Model model) {
        showDeleteAllConfirmation = true;
        model.addAttribute("showDeleteAllConfirmation", showDeleteAllConfirmation);
        return "index";
    }

    @PostMapping("/removeAllTodo")
    public String removeAllTodo(Model model) {
        try {
            activityService.removeAllFromTodoList();
            showDeleteAllConfirmation = false;
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "index";
        }
    }



    /** True when the time intervals of the two activities overlap. */
    private boolean overlaps(Activity a1, Activity a2) {
        int start1 = parseTimeToMinutes(a1.getStartTime());
        int end1 = start1 + Integer.parseInt(a1.getDuration());
        int start2 = parseTimeToMinutes(a2.getStartTime());
        int end2 = start2 + Integer.parseInt(a2.getDuration());

        return (start1 < end2 && end1 > start2);
    }


    private int parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Time is not in a valid format (HH:MM): " + time);
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Time is not numeric: " + time, e);
        }
    }



    @GetMapping("/showToggleConfirmation")
    public String showToggleConfirmation(@RequestParam int id, Model model) {
        model.addAttribute("showToggleConfirmation", true);
        model.addAttribute("toggleId", id);
        return "index";
    }

    @GetMapping("/showToggleConfirmation2")
    public String showToggleConfirmation2(@RequestParam int id, @RequestParam String action, Model model) {
        model.addAttribute("showToggleConfirmation2", true);
        model.addAttribute("toggleId", id);
        model.addAttribute("toggleAction", action);
        return "index";
    }



    /**
     * Changes the status of a to-do activity. An activity can only be started
     * or completed after all earlier activities are completed.
     */
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id, @RequestParam("status") String status, Model model) {
        try {
            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                throw new IllegalArgumentException("Activity not found.");
            }

            ActivityStatus newStatus = ActivityStatus.valueOf(status);
            if ((newStatus == ActivityStatus.IN_PROGRESS || newStatus == ActivityStatus.COMPLETED) &&
                    !activityService.areAllPreviousActivitiesCompleted(activity)) {
                model.addAttribute("errorMessage", "You must complete all previous activities before continuing.");
                return "index";
            }

            activity.setStatus(newStatus);
            if (newStatus == ActivityStatus.COMPLETED)
                activity.setProgress("100");
            if (newStatus == ActivityStatus.NOT_STARTED || newStatus == ActivityStatus.IN_PROGRESS)
                activity.setProgress("0");
            activityService.saveActivity(activity);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating the status: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/showUpdateProgress")
    public String showUpdateProgress(@RequestParam("id") int id, Model model) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found.");
        }

        model.addAttribute("activity", activity);
        model.addAttribute("showProgressForm", true);
        return "index";
    }

    @PostMapping("/updateProgress")
    public String updateProgress(@RequestParam("id") int id, @RequestParam("newProgress") String newProgress, Model model) {
        try {
            int progressValue;
            try {
                progressValue = Integer.parseInt(newProgress);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Progress must be a number.");
            }

            if (progressValue < 0 || progressValue > 100) {
                throw new IllegalArgumentException("Progress must be between 0 and 100.");
            }

            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                throw new IllegalArgumentException("Activity not found.");
            }

            activity.setProgress(newProgress);
            activityService.saveActivity(activity);

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("showProgressForm", true);
            model.addAttribute("activity", activityService.getActivityById(id));
            return "index";
        }
    }


}
