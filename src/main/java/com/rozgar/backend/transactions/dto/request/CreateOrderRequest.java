package com.rozgar.backend.transactions.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(

        @NotNull(message = "RFQ ID is required")
        Long rfqId,

        @NotNull(message = "Quote ID is required")
        Long quoteId
) {}
