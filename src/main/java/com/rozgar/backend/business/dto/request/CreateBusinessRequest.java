package com.rozgar.backend.business.dto.request;

import com.rozgar.backend.business.enums.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBusinessRequest (
        @NotBlank(message = "Business name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 to 100 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Business type is required")
        BusinessType businessType,

        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST number format"
        )
        String gstNumber,

        @Pattern(
                regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
                message = "Invalid PAN number format"
        )
        String panNumber,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "PinCode is required")
        String pincode,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Contact number is required")
        String phone,

        String website,

        @NotNull(message = "Bank account number is required")
        String bankAccountNumber,

        @NotNull(message = "IFSC Code is required")
        String ifscCode,

        @NotNull(message = "Account name is required")
        String accountHolderName,

        @NotNull(message = "Bank name is to be filled")
        String bankName
        )
{}
