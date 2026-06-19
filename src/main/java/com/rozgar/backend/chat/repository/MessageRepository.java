package com.rozgar.backend.chat.repository;

import com.rozgar.backend.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Paginated history for a thread
    Page<Message> findByThreadId(Long threadId, Pageable pageable);

    // Count unread messages for a user in a thread
    long countByThreadIdAndReadFalseAndSenderUserIdNot(Long threadId, Long userId);

    // Mark all messages in thread as read (except own)
    @Modifying
    @Query("UPDATE Message m SET m.read = true " +
            "WHERE m.thread.id = :threadId AND m.senderUserId != :userId")
    void markAllAsRead(Long threadId, Long userId);
}
