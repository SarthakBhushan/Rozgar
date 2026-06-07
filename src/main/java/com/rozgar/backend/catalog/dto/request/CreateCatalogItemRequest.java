package com.rozgar.backend.catalog.dto.request;

import com.rozgar.backend.catalog.enums.CatalogItemType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCatalogItemRequest(

        @NotBlank(message = "Item name is required")
        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
        String name,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Item type is required")
        CatalogItemType itemType,

        // optional — categoryId links to existing category
        Long categoryId,

        @NotNull(message = "Price per unit is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal pricePerUnit,

        @NotBlank(message = "Unit is required (e.g. kg, piece, meter, hour)")
        String unit,

        @Min(value = 1, message = "Minimum order quantity must be at least 1")
        Integer minOrderQuantity
) {}
