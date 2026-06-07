package com.rozgar.backend.catalog.dto.response;

import com.rozgar.backend.catalog.entity.CatalogItem;
import com.rozgar.backend.catalog.enums.CatalogItemStatus;
import com.rozgar.backend.catalog.enums.CatalogItemType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CatalogItemResponse(

        Long id,
        String name,
        String description,
        CatalogItemType itemType,
        CategoryResponse category,      // null if no category assigned
        BigDecimal pricePerUnit,
        String unit,
        Integer minOrderQuantity,
        Long businessId,
        CatalogItemStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CatalogItemResponse from(CatalogItem item) {
        return new CatalogItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getItemType(),
                item.getCategory() != null
                        ? CategoryResponse.from(item.getCategory()) : null,
                item.getPricePerUnit(),
                item.getUnit(),
                item.getMinOrderQuantity(),
                item.getBusinessId(),
                item.getStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
