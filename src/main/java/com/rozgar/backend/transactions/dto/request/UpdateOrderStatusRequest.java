package com.rozgar.backend.transactions.dto.request;

import com.rozgar.backend.transactions.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {}
