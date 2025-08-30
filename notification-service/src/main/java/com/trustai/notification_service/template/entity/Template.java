package com.trustai.notification_service.template.entity;

import java.util.Map;

public interface Template {
    Long getId();
    String getCode();

    /**
     * Updates the fields of the implementing entity based on the provided map of key-value pairs.
     * This removes the switch block from the service classes and localizes field control inside the entity itself.
     * <p>
     * This design helps centralize the logic for field updates within the entity itself, rather than
     * scattering switch or if-else blocks across service classes. It promotes better encapsulation
     * and makes the entity responsible for managing its own state.
     *
     * @param updates a map containing field names as keys and their corresponding new values as values
     * @throws IllegalArgumentException if an invalid or unsupported field name is provided
     */
    void updateFields(Map<String, String> updates);
}
