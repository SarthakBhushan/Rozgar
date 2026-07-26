package com.rozgar.backend.business.entity;

import com.rozgar.backend.business.enums.BusinessStatus;
import com.rozgar.backend.business.enums.BusinessType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @Column(unique = true, length = 15)
    private String gstNumber;

    @Column(unique = true, length = 10)
    private String panNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private String pincode;

    private String address;

    private String phone;

    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BusinessStatus status = BusinessStatus.PENDING;

    @Column(nullable = false)
    private Long ownerId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(length = 20)
    private String bankAccountNumber;

    @Column(length = 11)
    private String ifscCode;

    @Column(length = 100)
    private String accountHolderName;

    @Column(length = 50)
    private String bankName;

    @Column(unique = true)
    private String razorpayLinkedAccountId;
}
