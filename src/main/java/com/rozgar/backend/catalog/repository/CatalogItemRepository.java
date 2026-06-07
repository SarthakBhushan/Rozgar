package com.rozgar.backend.catalog.repository;

import com.rozgar.backend.catalog.entity.CatalogItem;
import com.rozgar.backend.catalog.enums.CatalogItemStatus;
import com.rozgar.backend.catalog.enums.CatalogItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

    Page<CatalogItem> findByBusinessId(Long businessId, Pageable pageable);

    Page<CatalogItem> findByBusinessIdAndStatus(
            Long businessId, CatalogItemStatus status, Pageable pageable);

    Page<CatalogItem> findByCategoryIdAndStatus(
            Long categoryId, CatalogItemStatus status, Pageable pageable);

    Page<CatalogItem> findByItemTypeAndStatus(
            CatalogItemType itemType, CatalogItemStatus status, Pageable pageable);


    long countByBusinessIdAndStatus(Long businessId, CatalogItemStatus status);
}
