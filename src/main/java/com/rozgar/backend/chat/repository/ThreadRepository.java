package com.rozgar.backend.chat.repository;

import com.rozgar.backend.chat.entity.ConversationThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<ConversationThread, Long> {

    Optional<ConversationThread> findByRfqId(Long rfqId);

    // All threads where user is buyer or seller
    List<ConversationThread> findByBuyerUserIdOrSellerUserId(Long buyerUserId, Long sellerUserId);

    boolean existsByRfqId(Long rfqId);
}
