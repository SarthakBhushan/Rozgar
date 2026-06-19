package com.rozgar.backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private ConversationThread thread;

    @Column(nullable = false)
    private Long senderUserId;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

}
