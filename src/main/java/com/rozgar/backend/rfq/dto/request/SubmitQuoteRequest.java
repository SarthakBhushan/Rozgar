package com.rozgar.backend.rfq.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubmitQuoteRequest(

        @NotNull(message = "Price per unit is required")
        @DecimalMin(value = "0.01", message = "Price has to be greater than 0")
        BigDecimal pricePerUnit,

        @NotNull(message = "Available quantity is required")
        @Min(value = 1, message = "Quantity must be greater than 1")
        Integer availableQuantity,

        @Size(max=1000, message = "Note cannot exceed 1000 characters")
        String note,

        LocalDateTime validUntil
) {}
