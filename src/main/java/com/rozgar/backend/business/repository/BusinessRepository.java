package com.rozgar.backend.business.repository;

import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.enums.BusinessStatus;
import com.rozgar.backend.business.enums.BusinessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    Optional<Business> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    @Query("SELECT b FROM Business b WHERE b.status = :status " +
            "AND (:type IS NULL OR b.businessType = :type) " +
            "AND (:city IS NULL OR LOWER(b.city) = LOWER(:city))")
    Page<Business> searchBusinesses(
            @Param("status") BusinessStatus status,
            @Param("type") BusinessType type,
            @Param("city") String city,
            Pageable pageable
    );
}
