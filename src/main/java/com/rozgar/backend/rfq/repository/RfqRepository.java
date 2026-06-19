package com.rozgar.backend.rfq.repository;

import com.rozgar.backend.rfq.entity.Rfq;
import com.rozgar.backend.rfq.enums.RfqStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, Long> {

    Page<Rfq> findByBuyerUserId(Long buyerUserId, Pageable pageable);

    Page<Rfq> findByBuyerUserIdAndStatus(Long buyerUserId, RfqStatus status, Pageable pageable);

    Page<Rfq> findByTargetSellerBusinessIdAndStatus(
            Long sellerBusinessId, RfqStatus status, Pageable pageable);

    Page<Rfq> findByStatusAndTargetSellerBusinessIdIsNull(
            RfqStatus status, Pageable pageable);
}
