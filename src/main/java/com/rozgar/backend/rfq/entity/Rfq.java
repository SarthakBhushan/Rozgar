package com.rozgar.backend.rfq.entity;


import com.rozgar.backend.rfq.enums.RfqStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfqs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rfq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;                   // short title e.g. "Need 500m cotton fabric"

    @Column(nullable = false, length = 2000)
    private String description;             // detailed requirement

    @Column(nullable = false)
    private String unit;                    // kg, meter, piece, etc.

    @Column(nullable = false)
    private Integer quantity;               // how many units needed

    @Column(precision = 12, scale = 2)
    private BigDecimal targetPrice;         // buyer's budget per unit (optional)

    private String deliveryLocation;        // city/address for delivery

    private LocalDateTime deadline;

    @Column(nullable = false)
    private Long buyerUserId;               // FK to users.id

    @Column(nullable = true)
    private Long buyerBusinessId;           // FK to businesses.id

    @Column
    private Long targetSellerBusinessId;    // null = open to all sellers

    @Column
    private Long catalogItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RfqStatus status = RfqStatus.OPEN;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.EAGER)
    @Builder.Default
    private List<Quote> quotes = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
