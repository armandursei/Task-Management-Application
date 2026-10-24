/**
 * JPA converter that persists {@link ActivityStatus} using its database code,
 * so rows already stored in the {@code status} column keep mapping correctly.
 * @author Ursei George-Armand
 */


package com.backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ActivityStatusConverter implements AttributeConverter<ActivityStatus, String> {

    @Override
    public String convertToDatabaseColumn(ActivityStatus status) {
        return status == null ? null : status.getDatabaseValue();
    }

    @Override
    public ActivityStatus convertToEntityAttribute(String value) {
        return value == null ? null : ActivityStatus.fromDatabaseValue(value);
    }
}
