package com.rozgar.backend.transactions.dto.response;

import com.rozgar.backend.transactions.entity.Payment;
import com.rozgar.backend.transactions.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,
        Long orderId,
        String razorpayOrderId,
        String razorpayPaymentId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment p){
        return new PaymentResponse(
                p.getId(),
                p.getOrder().getId(),
                p.getRazorpayOrderId(),
                p.getRazorpayPaymentId(),
                p.getAmount(),
                p.getCurrency(),
                p.getStatus(),
                p.getCreatedAt()

        );
    }
}
