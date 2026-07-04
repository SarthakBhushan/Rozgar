package com.rozgar.backend.transactions.controller;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.common.response.ApiResponse;
import com.rozgar.backend.common.response.PagedResponse;
import com.rozgar.backend.transactions.dto.request.CreateOrderRequest;
import com.rozgar.backend.transactions.dto.request.UpdateOrderStatusRequest;
import com.rozgar.backend.transactions.dto.request.VerifyPaymentRequest;
import com.rozgar.backend.transactions.dto.response.InvoiceResponse;
import com.rozgar.backend.transactions.dto.response.OrderResponse;
import com.rozgar.backend.transactions.dto.response.PaymentResponse;
import com.rozgar.backend.transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /api/v1/transactions/orders
    // Buyer creates order after RFQ is ACCEPTED
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal User currentUser) {
        OrderResponse response = transactionService.createOrder(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    // GET /api/v1/transactions/orders/{id}
    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success(transactionService.getOrderById(id, currentUser)));
    }

    // GET /api/v1/transactions/orders/my/buyer
    // Buyer sees all their orders
    @GetMapping("/orders/my/buyer")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getMyOrdersAsBuyer(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getMyOrdersAsBuyer(currentUser, page, size)));
    }

    // GET /api/v1/transactions/orders/my/seller
    // Seller sees incoming orders for their business
    @GetMapping("/orders/my/seller")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getMyOrdersAsSeller(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getMyOrdersAsSeller(currentUser, page, size)));
    }

    // PUT /api/v1/transactions/orders/{id}/status
    // Seller: CONFIRMED→PROCESSING→SHIPPED | Buyer: SHIPPED→DELIVERED | Either: CANCELLED
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success("Order status updated",
                        transactionService.updateOrderStatus(id, request, currentUser)));
    }

    // POST /api/v1/transactions/orders/{id}/payment/initiate
    // Buyer initiates Razorpay payment — returns razorpayOrderId for frontend
    @PostMapping("/orders/{id}/payment/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        PaymentResponse response = transactionService.initiatePayment(id, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated. Use razorpayOrderId in frontend.", response));
    }

    // POST /api/v1/transactions/orders/{id}/payment/verify
    // Called after frontend Razorpay checkout completes — verifies signature
    @PostMapping("/orders/{id}/payment/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @PathVariable Long id,
            @RequestBody @Valid VerifyPaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        PaymentResponse response = transactionService.verifyPayment(request, currentUser);
        return ResponseEntity.ok(
                ApiResponse.success("Payment verified. Invoice generated.", response));
    }

    // GET /api/v1/transactions/orders/{id}/payment
    @GetMapping("/orders/{id}/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getPaymentByOrderId(id, currentUser)));
    }

    // GET /api/v1/transactions/orders/{id}/invoice
    // Available only after payment SUCCESS
    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getInvoiceByOrderId(id, currentUser)));
    }
}
