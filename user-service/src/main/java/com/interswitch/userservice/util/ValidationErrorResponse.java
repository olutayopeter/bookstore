package com.interswitch.userservice.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorResponse {

    private Map<String, String> fieldErrors = new HashMap<>();

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String message) {
        fieldErrors.put(field, message);
    }

}
