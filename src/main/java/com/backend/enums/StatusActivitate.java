/**
 * Enum pentru definirea statusului activităților (Neîncepută, În Progres, Finalizată).
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */


package com.backend.enums;

public enum StatusActivitate {
    neinceputa("Neinceputa"),
    in_progres("In Progres"),
    finalizata("Finalizata");

    private final String databaseValue;

    StatusActivitate(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public static StatusActivitate fromDatabaseValue(String value) {
        for (StatusActivitate status : values()) {
            if (status.databaseValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + value);
    }
}
