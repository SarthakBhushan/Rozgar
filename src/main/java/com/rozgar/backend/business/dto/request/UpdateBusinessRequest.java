package com.rozgar.backend.business.dto.request;

import com.rozgar.backend.business.enums.BusinessType;
import jakarta.validation.constraints.Size;

public record UpdateBusinessRequest(

        @Size(min = 2, max = 100, message = "Name must be between 2 to 100 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        BusinessType businessType,

        String city,
        String state,
        String pincode,
        String address,
        String phone,
        String website,

        String bankAccountNumber,
        String ifscCode,
        String accountHolderName,
        String bankName

) {}
