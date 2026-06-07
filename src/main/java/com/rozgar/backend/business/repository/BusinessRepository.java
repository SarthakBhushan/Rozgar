package com.rozgar.backend.business.repository;

import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.enums.BusinessStatus;
import com.rozgar.backend.business.enums.BusinessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    Optional<Business> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    Page<Business> findByBusinessTypeAndStatus(
            BusinessType businessType, BusinessStatus status, Pageable pageable
    );

    Page<Business> findByCityIgnoreCaseAndStatus(
            String city, BusinessStatus status, Pageable pageable
    );

    Page<Business> findByStatus(BusinessStatus status, Pageable pageable);
}
