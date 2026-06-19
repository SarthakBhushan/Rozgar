package com.rozgar.backend.rfq.dto.response;

import com.rozgar.backend.rfq.entity.Quote;
import com.rozgar.backend.rfq.enums.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuoteResponse(

        Long id,
        Long rfqId,
        Long sellerUserId,
        Long sellerBusinessId,
        BigDecimal pricePerUnit,
        Integer availableQuantity,
        String note,
        LocalDateTime validUntil,
        QuoteStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static QuoteResponse from(Quote q) {
        return new QuoteResponse(
                q.getId(),
                q.getRfq().getId(),
                q.getSellerUserId(),
                q.getSellerBusinessId(),
                q.getPricePerUnit(),
                q.getAvailableQuantity(),
                q.getNote(),
                q.getValidUntil(),
                q.getStatus(),
                q.getCreatedAt(),
                q.getUpdatedAt()
        );
    }
}
