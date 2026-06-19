package com.rozgar.backend.rfq.repository;


import com.rozgar.backend.rfq.entity.Quote;
import com.rozgar.backend.rfq.enums.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByRfqId(Long rfqId);

    Optional<Quote> findByRfqIdAndSellerBusinessId(Long rfqId, Long sellerBusinessId);

    List<Quote> findBySellerBusinessId(Long sellerBusinessId);

    boolean existsByRfqIdAndSellerBusinessId(Long rfqId, Long sellerBusinessId);

    List<Quote> findByRfqIdAndStatus(Long rfqId, QuoteStatus status);
}
