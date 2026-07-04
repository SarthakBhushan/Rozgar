package com.rozgar.backend.transactions.repository;

import com.rozgar.backend.transactions.entity.Order;
import com.rozgar.backend.transactions.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByRfqId(Long rfqId);
    Optional<Order> findByQuoteId(Long quoteId);
    boolean existsByRfqId(Long rfqId);

    Page<Order> findByBuyerUserId(Long buyerUserId, Pageable pageable);
    Page<Order> findByBuyerUserIdAndStatus(Long buyerUserId, OrderStatus status, Pageable pageable);

    Page<Order> findBySellerBusinessId(Long sellerBusinessId, Pageable pageable);
    Page<Order> findBySellerBusinessIdAndStatus(Long sellerBusinessId, OrderStatus status, Pageable pageable);
}
