package com.rozgar.backend.transactions.dto.response;

import com.rozgar.backend.transactions.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        Long orderId,
        Long buyerUserId,
        Long sellerBusinessId,
        BigDecimal amount,
        BigDecimal gstAmount,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt
) {
    public static InvoiceResponse from(Invoice i) {
        return new InvoiceResponse(
                i.getId(), i.getInvoiceNumber(), i.getOrderId(),
                i.getBuyerUserId(), i.getSellerBusinessId(),
                i.getAmount(), i.getGstAmount(), i.getTotalAmount(),
                i.getCurrency(), i.getCreatedAt()
        );
    }
}
