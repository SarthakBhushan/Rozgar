package com.rozgar.backend.transactions.service;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.repository.BusinessRepository;
import com.rozgar.backend.common.exception.BadRequestException;
import com.rozgar.backend.common.exception.ForbiddenException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.PagedResponse;
import com.rozgar.backend.rfq.entity.Quote;
import com.rozgar.backend.rfq.entity.Rfq;
import com.rozgar.backend.rfq.enums.RfqStatus;
import com.rozgar.backend.rfq.repository.QuoteRepository;
import com.rozgar.backend.rfq.repository.RfqRepository;
import com.rozgar.backend.transactions.dto.request.CreateOrderRequest;
import com.rozgar.backend.transactions.dto.request.UpdateOrderStatusRequest;
import com.rozgar.backend.transactions.dto.request.VerifyPaymentRequest;
import com.rozgar.backend.transactions.dto.response.InvoiceResponse;
import com.rozgar.backend.transactions.dto.response.OrderResponse;
import com.rozgar.backend.transactions.dto.response.PaymentResponse;
import com.rozgar.backend.transactions.entity.Invoice;
import com.rozgar.backend.transactions.entity.Order;
import com.rozgar.backend.transactions.entity.Payment;
import com.rozgar.backend.transactions.enums.OrderStatus;
import com.rozgar.backend.transactions.enums.PaymentStatus;
import com.rozgar.backend.transactions.gateway.RazorpayGatewayService;
import com.rozgar.backend.transactions.repository.InvoiceRepository;
import com.rozgar.backend.transactions.repository.OrderRepository;
import com.rozgar.backend.transactions.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final RfqRepository rfqRepository;
    private final QuoteRepository quoteRepository;
    private final BusinessRepository businessRepository;
    private final RazorpayGatewayService razorpayGatewayService;

    //Helper
    private Order findOrderById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Order", String.valueOf(orderId)));
    }

    private Business getOwnedBusiness(User currentuser){
        return businessRepository.findByOwnerId(currentuser.getId())
                .orElseThrow(()-> new BadRequestException(
                        "You must have a registered business to view seller orders."));
    }

    private void validateOrderAccess(Order order, User currentUser){
        boolean isBuyer = order.getBuyerUserId().equals(currentUser.getId());
        boolean isSeller = order.getSellerUserId().equals(currentUser.getId());
        if(!isBuyer && !isSeller) throw new ForbiddenException("You are not a party to this order.");
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next, boolean isBuyer, boolean isSeller){
        switch (next) {
            case PROCESSING -> {
                if (!isSeller) throw new ForbiddenException("Only seller can mark PROCESSING.");
                if (current != OrderStatus.CONFIRMED)
                    throw new BadRequestException("Order must be CONFIRMED to mark PROCESSING.");
            }
            case SHIPPED -> {
                if (!isSeller) throw new ForbiddenException("Only seller can mark SHIPPED.");
                if (current != OrderStatus.PROCESSING)
                    throw new BadRequestException("Order must be PROCESSING to mark SHIPPED.");
            }
            case DELIVERED -> {
                if (!isBuyer) throw new ForbiddenException("Only buyer can confirm DELIVERED.");
                if (current != OrderStatus.SHIPPED)
                    throw new BadRequestException("Order must be SHIPPED to confirm DELIVERED.");
            }
            case CANCELLED -> {
                if (current == OrderStatus.DELIVERED)
                    throw new BadRequestException("Cannot cancel a delivered order.");
            }
            default -> throw new BadRequestException("Invalid status transition to: " + next);
        }
    }

    //Create Order
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, User currentUser) {
        Rfq rfq = rfqRepository.findById(request.rfqId())
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", String.valueOf(request.rfqId())));

        if (rfq.getStatus() != RfqStatus.ACCEPTED) {
            throw new BadRequestException(
                    "Order can only be created for an ACCEPTED RFQ. Current: " + rfq.getStatus());
        }
        if (!rfq.getBuyerUserId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the RFQ buyer can create an order.");
        }
        if (orderRepository.existsByRfqId(request.rfqId())) {
            throw new BadRequestException("An order already exists for this RFQ.");
        }

        Quote quote = quoteRepository.findById(request.quoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Quote", String.valueOf(request.quoteId())));

        if (!quote.getRfq().getId().equals(request.rfqId())) {
            throw new BadRequestException("This quote does not belong to the specified RFQ.");
        }

        BigDecimal totalAmount = quote.getPricePerUnit()
                .multiply(BigDecimal.valueOf(rfq.getQuantity()));

        Order order = Order.builder()
                .rfqId(rfq.getId())
                .quoteId(quote.getId())
                .buyerUserId(currentUser.getId())
                .sellerUserId(quote.getSellerUserId())
                .sellerBusinessId(quote.getSellerBusinessId())
                .quantity(rfq.getQuantity())
                .pricePerUnit(quote.getPricePerUnit())
                .totalAmount(totalAmount)
                .unit(rfq.getUnit())
                .deliveryLocation(rfq.getDeliveryLocation())
                .status(OrderStatus.CONFIRMED)
                .build();

        return OrderResponse.from(orderRepository.save(order));
    }

    //Get order by ID

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, User currentUser) {
        Order order = findOrderById(orderId);
        validateOrderAccess(order, currentUser);
        return OrderResponse.from(order);
    }

    //My orders as buyer

    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrdersAsBuyer(User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                orderRepository.findByBuyerUserId(currentUser.getId(), pageable)
                        .map(OrderResponse::from));
    }

    //My orders as seller

    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrdersAsSeller(User currentUser, int page, int size) {
        Business business = getOwnedBusiness(currentUser);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                orderRepository.findBySellerBusinessId(business.getId(), pageable)
                        .map(OrderResponse::from));
    }

    //Update order status

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, User currentUser) {
        Order order = findOrderById(orderId);
        boolean isBuyer  = order.getBuyerUserId().equals(currentUser.getId());
        boolean isSeller = order.getSellerUserId().equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new ForbiddenException("You are not a party to this order.");
        }

        validateStatusTransition(order.getStatus(), request.status(), isBuyer, isSeller);
        order.setStatus(request.status());
        return OrderResponse.from(orderRepository.save(order));
    }

    //Initiate payment

    @Transactional
    public PaymentResponse initiatePayment(Long orderId, User currentUser) {
        Order order = findOrderById(orderId);

        if (!order.getBuyerUserId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the buyer can initiate payment.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot initiate payment for a cancelled order.");
        }

        // Return existing pending payment if already initiated
        return paymentRepository.findByOrderId(orderId).map(existing -> {
            if (existing.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("Payment already completed for this order.");
            }
            return PaymentResponse.from(existing);
        }).orElseGet(() -> {
            String razorpayOrderId;
            try {
                razorpayOrderId = razorpayGatewayService.createRazorpayOrder(
                        order.getTotalAmount(), "INR", "rozgar-order-" + orderId);
            } catch (Exception e) {
                log.error("Razorpay order creation failed for order {}", orderId, e);
                throw new BadRequestException("Payment initiation failed. Please try again.");
            }

            Payment payment = Payment.builder()
                    .order(order)
                    .razorpayOrderId(razorpayOrderId)
                    .amount(order.getTotalAmount())
                    .currency("INR")
                    .status(PaymentStatus.PENDING)
                    .build();

            return PaymentResponse.from(paymentRepository.save(payment));
        });
    }

    //Verify payment

    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request, User currentUser) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for Razorpay order: " + request.razorpayOrderId()));

        if (!payment.getOrder().getBuyerUserId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not the buyer for this payment.");
        }

        boolean valid = razorpayGatewayService.verifySignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature());

        if (!valid) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BadRequestException("Payment signature verification failed.");
        }

        payment.setRazorpayPaymentId(request.razorpayPaymentId());
        payment.setRazorpaySignature(request.razorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        try {
            Business sellerBusiness = businessRepository
                    .findById(payment.getOrder().getSellerBusinessId()).orElse(null);
            if (sellerBusiness != null && sellerBusiness.getRazorpayLinkedAccountId() != null) {
                BigDecimal sellerAmount = payment.getAmount()
                        .multiply(new BigDecimal("0.97")); // 3% platform fee
                razorpayGatewayService.transferToSeller(
                        payment.getRazorpayPaymentId(),
                        sellerBusiness.getRazorpayLinkedAccountId(),
                        sellerAmount);
            }
        } catch (Exception e) {
            log.error("Transfer to seller failed for order {}", payment.getOrder().getId(), e);
            // Don't fail payment — log and handle manually
        }
        paymentRepository.save(payment);

        generateInvoice(payment.getOrder());
        return PaymentResponse.from(payment);
    }

    //Get payment for order

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, User currentUser) {
        Order order = findOrderById(orderId);
        validateOrderAccess(order, currentUser);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment found for order: " + orderId));
        return PaymentResponse.from(payment);
    }

    //Get invoice for order

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByOrderId(Long orderId, User currentUser) {
        Order order = findOrderById(orderId);
        validateOrderAccess(order, currentUser);
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found. Payment may not be completed yet."));
        return InvoiceResponse.from(invoice);
    }

    //Internal: generate invoice after payment

    private void generateInvoice(Order order) {
        if (invoiceRepository.findByOrderId(order.getId()).isPresent()) return;

        String invoiceNumber = "INV-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"))
                + "-" + String.format("%05d", order.getId());

        BigDecimal gstRate   = new BigDecimal("0.18");
        BigDecimal gstAmount = order.getTotalAmount().multiply(gstRate);
        BigDecimal total     = order.getTotalAmount().add(gstAmount);

        invoiceRepository.save(Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .orderId(order.getId())
                .buyerUserId(order.getBuyerUserId())
                .sellerBusinessId(order.getSellerBusinessId())
                .amount(order.getTotalAmount())
                .gstAmount(gstAmount)
                .totalAmount(total)
                .currency("INR")
                .build());

        log.info("Invoice generated: {} for order {}", invoiceNumber, order.getId());
    }
}
