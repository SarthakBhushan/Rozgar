package com.rozgar.backend.catalog.dto.response;

import com.rozgar.backend.catalog.entity.Category;

public record CategoryResponse(

        Long id,
        String name,
        String description
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
