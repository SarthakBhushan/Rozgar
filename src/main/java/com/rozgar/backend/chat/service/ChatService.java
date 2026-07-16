package com.rozgar.backend.chat.service;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.repository.BusinessRepository;
import com.rozgar.backend.chat.dto.request.SendMessageRequest;
import com.rozgar.backend.chat.dto.response.MessageResponse;
import com.rozgar.backend.chat.dto.response.ThreadResponse;
import com.rozgar.backend.chat.entity.ConversationThread;
import com.rozgar.backend.chat.entity.Message;
import com.rozgar.backend.chat.repository.MessageRepository;
import com.rozgar.backend.chat.repository.ThreadRepository;
import com.rozgar.backend.common.exception.BadRequestException;
import com.rozgar.backend.common.exception.ForbiddenException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.PagedResponse;
import com.rozgar.backend.rfq.entity.Rfq;
import com.rozgar.backend.rfq.repository.QuoteRepository;
import com.rozgar.backend.rfq.repository.RfqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final RfqRepository rfqRepository;
    private final BusinessRepository businessRepository;
    private final QuoteRepository quoteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Create or get thread for an RFQ ──────────────────────────────────────
    // Called when a quote is submitted — opens the negotiation thread

    @Transactional
    public ThreadResponse getOrCreateThread(Long rfqId, User currentUser) {
        // Return existing thread if already created
        if (threadRepository.existsByRfqId(rfqId)) {
            ConversationThread existing = threadRepository.findByRfqId(rfqId).get();
            validateThreadAccess(existing, currentUser);
            return ThreadResponse.from(existing);
        }

        Rfq rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", String.valueOf(rfqId)));

        boolean isBuyer = rfq.getBuyerUserId().equals(currentUser.getId());
        if(!isBuyer){
            businessRepository.findByOwnerId(currentUser.getId())
                    .orElseThrow(()-> new BadRequestException("You must have a registered business to start a negotiation thread."));
        }

        Business sellerBusiness = isBuyer ?
                findSellerBusinessForRfq(rfq) : businessRepository.findByOwnerId(currentUser.getId()).orElseThrow();

        Long sellerUserId = isBuyer ?
                findSellerUserIdForRfq(rfq) : currentUser.getId();

        ConversationThread thread = ConversationThread.builder()
                .rfqId(rfqId)
                .buyerUserId(rfq.getBuyerUserId())
                .sellerUserId(sellerUserId)
                .buyerBusinessId(rfq.getBuyerBusinessId())
                .sellerBusinessId(sellerBusiness.getId())
                .build();

        return ThreadResponse.from(threadRepository.save(thread));
    }

    // ── Get thread by RFQ ID ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ThreadResponse getThreadByRfqId(Long rfqId, User currentUser) {
        ConversationThread thread = threadRepository.findByRfqId(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No conversation thread found for RFQ: " + rfqId));
        validateThreadAccess(thread, currentUser);
        return ThreadResponse.from(thread);
    }

    // ── Get all threads for current user ──────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ThreadResponse> getMyThreads(User currentUser) {
        return threadRepository
                .findByBuyerUserIdOrSellerUserId(currentUser.getId(), currentUser.getId())
                .stream()
                .map(ThreadResponse::from)
                .toList();
    }

    // ── Send message via REST (persists + broadcasts over WebSocket) ──────────

    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, User currentUser) {
        ConversationThread thread = threadRepository.findById(request.threadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread", String.valueOf(request.threadId())));

        validateThreadAccess(thread, currentUser);

        Message message = Message.builder()
                .thread(thread)
                .senderUserId(currentUser.getId())
                .senderName(currentUser.getName())
                .content(request.content())
                .read(false)
                .build();

        Message saved = messageRepository.save(message);
        MessageResponse response = MessageResponse.from(saved);

        // Broadcast to all subscribers of this thread's topic
        messagingTemplate.convertAndSend(
                "/topic/thread/" + thread.getId(), response);

        return response;
    }

    // ── Get message history (paginated) ──────────────────────────────────────

    @Transactional
    public PagedResponse<MessageResponse> getHistory(Long threadId, User currentUser, int page, int size) {
        ConversationThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread", String.valueOf(threadId)));

        validateThreadAccess(thread, currentUser);

        // Mark messages as read when history is fetched
        messageRepository.markAllAsRead(threadId, currentUser.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        return PagedResponse.from(
                messageRepository.findByThreadId(threadId, pageable)
                        .map(MessageResponse::from));
    }

    // ── Unread count for a thread ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public long getUnreadCount(Long threadId, User currentUser) {
        return messageRepository.countByThreadIdAndReadFalseAndSenderUserIdNot(
                threadId, currentUser.getId());
    }

    // ── Validate thread access — only buyer and seller can access ─────────────

    private void validateThreadAccess(ConversationThread thread, User currentUser) {
        boolean isBuyer = thread.getBuyerUserId().equals(currentUser.getId());
        boolean isSeller = thread.getSellerUserId().equals(currentUser.getId());
        if (!isBuyer && !isSeller) {
            throw new ForbiddenException("You are not a participant in this conversation.");
        }
    }

    private Long findSellerUserIdForRfq(Rfq rfq) {
        return quoteRepository.findByRfqIdAndStatus(rfq.getId(),
                        com.rozgar.backend.rfq.enums.QuoteStatus.ACCEPTED)
                .stream().findFirst()
                .map(q -> q.getSellerUserId())
                .orElseThrow(() -> new BadRequestException(
                        "No accepted quote found for this RFQ. Accept a quote first."));
    }

    private Business findSellerBusinessForRfq(Rfq rfq) {
        Long sellerBusinessId = quoteRepository.findByRfqIdAndStatus(rfq.getId(),
                        com.rozgar.backend.rfq.enums.QuoteStatus.ACCEPTED)
                .stream().findFirst()
                .map(q -> q.getSellerBusinessId())
                .orElseThrow(() -> new BadRequestException(
                        "No accepted quote found for this RFQ. Accept a quote first."));
        return businessRepository.findById(sellerBusinessId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller business", String.valueOf(sellerBusinessId)));
    }
}

