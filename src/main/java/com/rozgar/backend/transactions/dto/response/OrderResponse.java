package com.rozgar.backend.transactions.dto.response;

import com.rozgar.backend.transactions.entity.Order;
import com.rozgar.backend.transactions.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long rfqId,
        Long quoteId,
        Long buyerUserId,
        Long sellerUserId,
        Long sellerBusinessId,
        Integer quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalAmount,
        String unit,
        String deliveryLocation,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderResponse from(Order o){
        return new OrderResponse(
                o.getId(),
                o.getRfqId(),
                o.getQuoteId(),
                o.getBuyerUserId(),
                o.getSellerUserId(),
                o.getSellerBusinessId(),
                o.getQuantity(),
                o.getPricePerUnit(),
                o.getTotalAmount(),
                o.getUnit(),
                o.getDeliveryLocation(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
