package com.rozgar.backend.catalog.service;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.repository.BusinessRepository;
import com.rozgar.backend.catalog.dto.request.CreateCatalogItemRequest;
import com.rozgar.backend.catalog.dto.request.UpdateCatalogItemRequest;
import com.rozgar.backend.catalog.dto.response.CatalogItemResponse;
import com.rozgar.backend.catalog.entity.CatalogItem;
import com.rozgar.backend.catalog.entity.Category;
import com.rozgar.backend.catalog.enums.CatalogItemStatus;
import com.rozgar.backend.catalog.enums.CatalogItemType;
import com.rozgar.backend.catalog.repository.CatalogItemRepository;
import com.rozgar.backend.catalog.repository.CategoryRepository;
import com.rozgar.backend.common.exception.BadRequestException;
import com.rozgar.backend.common.exception.ForbiddenException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogItemRepository catalogItemRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;

    // ── Add item ─────────────────────────────────────────────────────────────

    @Transactional
    public CatalogItemResponse addItem(CreateCatalogItemRequest request, User currentUser) {
        Business business = getOwnedBusiness(currentUser);

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category", String.valueOf(request.categoryId())));
        }

        CatalogItem item = CatalogItem.builder()
                .name(request.name())
                .description(request.description())
                .itemType(request.itemType())
                .category(category)
                .pricePerUnit(request.pricePerUnit())
                .unit(request.unit())
                .minOrderQuantity(request.minOrderQuantity() != null
                        ? request.minOrderQuantity() : 1)
                .businessId(business.getId())
                .status(CatalogItemStatus.ACTIVE)
                .build();

        return CatalogItemResponse.from(catalogItemRepository.save(item));
    }

    // ── Get single item ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CatalogItemResponse getById(Long id) {
        return CatalogItemResponse.from(findById(id));
    }

    // ── Update item ───────────────────────────────────────────────────────────

    @Transactional
    public CatalogItemResponse updateItem(Long id, UpdateCatalogItemRequest request, User currentUser) {
        CatalogItem item = findById(id);
        Business business = getOwnedBusiness(currentUser);

        if (!item.getBusinessId().equals(business.getId())) {
            throw new ForbiddenException("This item does not belong to your business.");
        }

        if (request.name()             != null) item.setName(request.name());
        if (request.description()      != null) item.setDescription(request.description());
        if (request.itemType()         != null) item.setItemType(request.itemType());
        if (request.pricePerUnit()     != null) item.setPricePerUnit(request.pricePerUnit());
        if (request.unit()             != null) item.setUnit(request.unit());
        if (request.minOrderQuantity() != null) item.setMinOrderQuantity(request.minOrderQuantity());
        if (request.status()           != null) item.setStatus(request.status());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category", String.valueOf(request.categoryId())));
            item.setCategory(category);
        }

        return CatalogItemResponse.from(catalogItemRepository.save(item));
    }

    // ── Delete item ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteItem(Long id, User currentUser) {
        CatalogItem item = findById(id);
        Business business = getOwnedBusiness(currentUser);

        if (!item.getBusinessId().equals(business.getId())) {
            throw new ForbiddenException("This item does not belong to your business.");
        }

        catalogItemRepository.delete(item);
    }

    // ── My catalog (owner — all statuses) ────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<CatalogItemResponse> getMyCatalog(User currentUser, int page, int size) {
        Business business = getOwnedBusiness(currentUser);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                catalogItemRepository
                        .findByBusinessId(business.getId(), pageable)
                        .map(CatalogItemResponse::from));
    }

    // ── Business catalog (buyer — active only) ────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<CatalogItemResponse> getBusinessCatalog(Long businessId, int page, int size) {
        // Verify business exists
        businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Business", String.valueOf(businessId)));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                catalogItemRepository
                        .findByBusinessIdAndStatus(businessId, CatalogItemStatus.ACTIVE, pageable)
                        .map(CatalogItemResponse::from));
    }

    // ── Browse by category ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<CatalogItemResponse> browseByCategory(Long categoryId, int page, int size) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", String.valueOf(categoryId)));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                catalogItemRepository
                        .findByCategoryIdAndStatus(categoryId, CatalogItemStatus.ACTIVE, pageable)
                        .map(CatalogItemResponse::from));
    }

    // ── Browse by item type ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<CatalogItemResponse> browseByType(CatalogItemType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                catalogItemRepository
                        .findByItemTypeAndStatus(type, CatalogItemStatus.ACTIVE, pageable)
                        .map(CatalogItemResponse::from));
    }

    // ── Internal helpers

    private CatalogItem findById(Long id) {
        return catalogItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CatalogItem", String.valueOf(id)));
    }

    private Business getOwnedBusiness(User currentUser) {
        return businessRepository.findByOwnerId(currentUser.getId())
                .orElseThrow(() -> new BadRequestException(
                        "You must register a business before managing catalog items."));
    }
}