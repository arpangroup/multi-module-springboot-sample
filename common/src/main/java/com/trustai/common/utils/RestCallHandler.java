package com.trustai.common.utils;

import com.trustai.common.exceptions.RestCallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

@Slf4j
public class RestCallHandler {
    public static <T> T handleRestCall(Supplier<T> action, String actionDescription) {
        try {
            return action.get();
        } catch (RestClientException e) {
            log.error("REST call failed during {}: {}", actionDescription, e.getMessage(), e);
            throw new RestCallException("Failed during " + actionDescription, e);  // Or your custom exception
        }
    }

    // Overloaded version without actionDescription
    public static <T> T handleRestCall(Supplier<T> action) {
        try {
            return action.get();
        } catch (RestClientException e) {
            log.error("REST call failed: {}", e.getMessage(), e);
            throw new RestCallException("REST call failed", e); // Or your custom exception
        }
    }
}
