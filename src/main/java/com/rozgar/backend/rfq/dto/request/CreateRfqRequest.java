package com.rozgar.backend.rfq.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateRfqRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,

        @NotBlank(message = "Unit is required")
        String unit,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @DecimalMin(value = "0.01", message = "Target price must be greater than 0")
        BigDecimal targetPrice,

        String deliveryLocation,

        LocalDateTime deadline,

        Long targetSellerBusinessId,

        Long catalogItemId
) {}
