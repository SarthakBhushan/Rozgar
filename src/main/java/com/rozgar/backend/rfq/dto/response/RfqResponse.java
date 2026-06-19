package com.rozgar.backend.rfq.dto.response;

import com.rozgar.backend.rfq.entity.Rfq;
import com.rozgar.backend.rfq.enums.RfqStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RfqResponse(

        Long id,
        String title,
        String description,
        String unit,
        Integer quantity,
        BigDecimal targetPrice,
        String deliveryLocation,
        LocalDateTime deadline,
        Long buyerUserId,
        Long buyerBusinessId,
        Long targetSellerBusinessId,
        Long catalogItemId,
        RfqStatus status,
        List<QuoteResponse> quotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RfqResponse from(Rfq rfq) {
        return new RfqResponse(
                rfq.getId(),
                rfq.getTitle(),
                rfq.getDescription(),
                rfq.getUnit(),
                rfq.getQuantity(),
                rfq.getTargetPrice(),
                rfq.getDeliveryLocation(),
                rfq.getDeadline(),
                rfq.getBuyerUserId(),
                rfq.getBuyerBusinessId(),
                rfq.getTargetSellerBusinessId(),
                rfq.getCatalogItemId(),
                rfq.getStatus(),
                rfq.getQuotes().stream().map(QuoteResponse::from).toList(),
                rfq.getCreatedAt(),
                rfq.getUpdatedAt()
        );
    }
}
