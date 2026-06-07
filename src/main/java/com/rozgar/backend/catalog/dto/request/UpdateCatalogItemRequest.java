package com.rozgar.backend.catalog.dto.request;

import com.rozgar.backend.catalog.enums.CatalogItemStatus;
import com.rozgar.backend.catalog.enums.CatalogItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateCatalogItemRequest(

        @Size(min = 2, max = 150)
        String name,

        @Size(max = 2000)
        String description,

        CatalogItemType itemType,

        Long categoryId,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal pricePerUnit,

        String unit,

        @Min(value = 1)
        Integer minOrderQuantity,

        CatalogItemStatus status
) {}
