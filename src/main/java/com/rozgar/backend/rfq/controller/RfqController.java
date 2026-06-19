package com.rozgar.backend.rfq.controller;

import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.common.response.ApiResponse;
import com.rozgar.backend.common.response.PagedResponse;
import com.rozgar.backend.rfq.dto.request.CreateRfqRequest;
import com.rozgar.backend.rfq.dto.request.SubmitQuoteRequest;
import com.rozgar.backend.rfq.dto.response.QuoteResponse;
import com.rozgar.backend.rfq.dto.response.RfqResponse;
import com.rozgar.backend.rfq.service.RfqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rfq")
@RequiredArgsConstructor
public class RfqController {

    private final RfqService rfqService;

    //Buyer post new rfq
    @PostMapping
    public ResponseEntity<ApiResponse<RfqResponse>> postRfq(
            @RequestBody @Valid CreateRfqRequest request,
            @AuthenticationPrincipal User currentUser){

        RfqResponse response = rfqService.postRfq(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("RFQ posted successfully", response));
    }

    //Get rfq by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RfqResponse>> getById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(rfqService.getById(id)));
    }

    //List al posted rfq for buyer
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PagedResponse<RfqResponse>>> getMyRfqs(
        @AuthenticationPrincipal User currentUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size){

        return ResponseEntity.ok(
                ApiResponse.success(rfqService.getMyRfqs(currentUser, page, size)));
    }

    //Open rfq to all seller
    @GetMapping("/open")
    public ResponseEntity<ApiResponse<PagedResponse<RfqResponse>>> getOpenRfqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        return ResponseEntity.ok(
                ApiResponse.success(rfqService.getOpenRfqs(page, size)));
    }

    //Buyer cancel rfq
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RfqResponse>> cancelRfq(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success("RFQ cancelled", rfqService.cancelRfq(id, currentUser)));
    }

    //Seller submit quote on Rfq
    @PostMapping("/{id}/quote")
    public ResponseEntity<ApiResponse<QuoteResponse>> submitQuote(
            @PathVariable Long id,
            @RequestBody @Valid SubmitQuoteRequest request,
            @AuthenticationPrincipal User currentUser) {

        QuoteResponse response = rfqService.submitQuote(id, request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quote submitted successfully", response));
    }

    //Buyer view quote on their rfq
    @GetMapping("/{id}/quotes")
    public ResponseEntity<ApiResponse<List<QuoteResponse>>> getQuotes(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(rfqService.getQuotesForRfq(id, currentUser)));
    }

    //Buyer accepts specific quote
    @PutMapping("/{rfqId}/quote/{quoteId}/accept")
    public ResponseEntity<ApiResponse<RfqResponse>> acceptQuote(
            @PathVariable Long rfqId,
            @PathVariable Long quoteId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success("Quote accepted — RFQ closed",
                        rfqService.acceptQuote(rfqId, quoteId, currentUser)));
    }

    //Buyer rejects a specific quote
    @PutMapping("/{rfqId}/quote/{quoteId}/reject")
    public ResponseEntity<ApiResponse<QuoteResponse>> rejectQuote(
            @PathVariable Long rfqId,
            @PathVariable Long quoteId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success("Quote rejected",
                        rfqService.rejectQuote(rfqId, quoteId, currentUser)));
    }
}
