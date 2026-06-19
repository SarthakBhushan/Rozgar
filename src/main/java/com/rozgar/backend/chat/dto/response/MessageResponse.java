package com.rozgar.backend.chat.dto.response;

import com.rozgar.backend.chat.entity.Message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long threadId,
        Long senderUserId,
        String senderName,
        String content,
        boolean read,
        LocalDateTime sentAt
) {
    public static MessageResponse from(Message m){
        return new MessageResponse(
                m.getId(),
                m.getThread().getId(),
                m.getSenderUserId(),
                m.getSenderName(),
                m.getContent(),
                m.isRead(),
                m.getSentAt()
        );
    }
}
