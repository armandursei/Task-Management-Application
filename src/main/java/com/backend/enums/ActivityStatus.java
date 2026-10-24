/**
 * Status of an activity in the to-do list (Not Started, In Progress, Completed).
 * Each constant has a stable database code and a human-readable label for the UI.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */


package com.backend.enums;

public enum ActivityStatus {
    NOT_STARTED("neinceputa", "Not Started"),
    IN_PROGRESS("in_progres", "In Progress"),
    COMPLETED("finalizata", "Completed");

    private final String databaseValue;
    private final String label;

    ActivityStatus(String databaseValue, String label) {
        this.databaseValue = databaseValue;
        this.label = label;
    }

    /** Code stored in the {@code status} column. */
    public String getDatabaseValue() {
        return databaseValue;
    }

    /** Label shown in the user interface. */
    public String getLabel() {
        return label;
    }

    public static ActivityStatus fromDatabaseValue(String value) {
        for (ActivityStatus status : values()) {
            if (status.databaseValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + value);
    }
}
