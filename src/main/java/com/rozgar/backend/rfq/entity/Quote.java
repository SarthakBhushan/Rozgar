package com.rozgar.backend.rfq.entity;


import com.rozgar.backend.rfq.enums.QuoteStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private Rfq rfq;

    @Column(nullable = false)
    private Long sellerUserId;

    @Column(nullable = false)
    private Long sellerBusinessId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(length = 1000)
    private String note;

    private LocalDateTime validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.PENDING;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
