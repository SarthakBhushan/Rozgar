package com.rozgar.backend.chat.dto.response;

import com.rozgar.backend.chat.entity.ConversationThread;

import java.time.LocalDateTime;

public record ThreadResponse(

        Long id,
        Long rfqId,
        Long buyerUserId,
        Long sellerUserId,
        Long buyerBusinessId,
        Long sellerBusinessId,
        LocalDateTime createdAt
) {

    public static ThreadResponse from(ConversationThread t){
        return new ThreadResponse(
                t.getId(),
                t.getRfqId(),
                t.getBuyerUserId(),
                t.getSellerUserId(),
                t.getBuyerBusinessId(),
                t.getSellerBusinessId(),
                t.getCreatedAt()
        );
    }
}
