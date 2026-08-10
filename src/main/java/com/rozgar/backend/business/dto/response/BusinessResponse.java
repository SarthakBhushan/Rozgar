package com.rozgar.backend.business.dto.response;

import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.enums.BusinessStatus;
import com.rozgar.backend.business.enums.BusinessType;

import java.time.LocalDateTime;

public record BusinessResponse(
        Long id,
        String name,
        String description,
        BusinessType businessType,
        String gstNumber,
        String panNumber,
        String city,
        String state,
        String pincode,
        String address,
        String phone,
        String website,
        String bankAccountNumber,
        String ifscCode,
        String accountHolderName,
        String bankName,
        BusinessStatus status,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BusinessResponse from(Business b){
        return new BusinessResponse(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getBusinessType(),
                b.getGstNumber(),
                b.getPanNumber(),
                b.getCity(),
                b.getState(),
                b.getPincode(),
                b.getAddress(),
                b.getPhone(),
                b.getWebsite(),
                b.getBankAccountNumber(),
                b.getIfscCode(),
                b.getAccountHolderName(),
                b.getBankName(),
                b.getStatus(),
                b.getOwnerId(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
