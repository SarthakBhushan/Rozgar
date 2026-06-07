package com.rozgar.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;
    private final Map<String, String> errors;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data,
                        String errorCode, Map<String, String> errors) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.errorCode = errorCode;
        this.errors    = errors;
        this.timestamp = Instant.now();
    }

    // ── Success ──────────────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, null);
    }

    // ── Error ────────────────────────────────────────────────────────────────

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, "INTERNAL_ERROR", null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode, null);
    }

    public static <T> ApiResponse<T> validationError(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, "VALIDATION_ERROR", errors);
    }
}