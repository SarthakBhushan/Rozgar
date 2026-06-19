package com.rozgar.backend.chat.controller;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.chat.dto.request.SendMessageRequest;
import com.rozgar.backend.chat.dto.response.MessageResponse;
import com.rozgar.backend.chat.dto.response.ThreadResponse;
import com.rozgar.backend.chat.service.ChatService;
import com.rozgar.backend.common.response.ApiResponse;
import com.rozgar.backend.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ── WebSocket handler ─────────────────────────────────────────────────────
    // Frontend sends to: /app/chat
    @MessageMapping("/chat")
    public void handleWebSocketMessage(
            @Payload SendMessageRequest request,
            Principal principal) {
        // principal.getName() = email from JWT
        // For WebSocket we handle via REST sendMessage which also broadcasts
        // This handler is for pure WebSocket clients (e.g. mobile)
    }

    // ── POST /api/v1/chat/thread/{rfqId} ──────────────────────────────────────
    // Create or get thread for an RFQ (seller initiates after quoting)
    @PostMapping("/thread/{rfqId}")
    public ResponseEntity<ApiResponse<ThreadResponse>> getOrCreateThread(
            @PathVariable Long rfqId,
            @AuthenticationPrincipal User currentUser) {

        ThreadResponse response = chatService.getOrCreateThread(rfqId, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thread ready", response));
    }

    // ── GET /api/v1/chat/thread/{rfqId} ───────────────────────────────────────
    // Get existing thread by RFQ ID
    @GetMapping("/thread/{rfqId}")
    public ResponseEntity<ApiResponse<ThreadResponse>> getThread(
            @PathVariable Long rfqId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(chatService.getThreadByRfqId(rfqId, currentUser)));
    }

    // ── GET /api/v1/chat/threads ──────────────────────────────────────────────
    // Get all threads for current user
    @GetMapping("/threads")
    public ResponseEntity<ApiResponse<List<ThreadResponse>>> getMyThreads(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(chatService.getMyThreads(currentUser)));
    }

    // ── POST /api/v1/chat/send ────────────────────────────────────────────────
    // Send a message via REST — persists and broadcasts over WebSocket
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestBody @Valid SendMessageRequest request,
            @AuthenticationPrincipal User currentUser) {

        MessageResponse response = chatService.sendMessage(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent", response));
    }

    // ── GET /api/v1/chat/history/{threadId} ───────────────────────────────────
    // Get paginated message history — also marks messages as read
    @GetMapping("/history/{threadId}")
    public ResponseEntity<ApiResponse<PagedResponse<MessageResponse>>> getHistory(
            @PathVariable Long threadId,
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        chatService.getHistory(threadId, currentUser, page, size)));
    }

    // ── GET /api/v1/chat/unread/{threadId} ────────────────────────────────────
    // Get unread message count for a thread
    @GetMapping("/unread/{threadId}")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable Long threadId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(chatService.getUnreadCount(threadId, currentUser)));
    }
}
