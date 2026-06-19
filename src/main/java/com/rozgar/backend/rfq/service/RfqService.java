package com.rozgar.backend.rfq.service;


import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.repository.BusinessRepository;
import com.rozgar.backend.common.exception.BadRequestException;
import com.rozgar.backend.common.exception.ForbiddenException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.PagedResponse;
import com.rozgar.backend.rfq.dto.request.CreateRfqRequest;
import com.rozgar.backend.rfq.dto.request.SubmitQuoteRequest;
import com.rozgar.backend.rfq.dto.response.QuoteResponse;
import com.rozgar.backend.rfq.dto.response.RfqResponse;
import com.rozgar.backend.rfq.entity.Quote;
import com.rozgar.backend.rfq.entity.Rfq;
import com.rozgar.backend.rfq.enums.QuoteStatus;
import com.rozgar.backend.rfq.enums.RfqStatus;
import com.rozgar.backend.rfq.repository.QuoteRepository;
import com.rozgar.backend.rfq.repository.RfqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RfqService {

    private final RfqRepository rfqRepository;
    private final QuoteRepository quoteRepository;
    private final BusinessRepository businessRepository;


    //Internal methods
    private Rfq findByID(Long id){
        return rfqRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "RFQ", String.valueOf(id)));
    }

    private Business getOwnedBusiness(User currentUser){
        return businessRepository.findByOwnerId(currentUser.getId())
                .orElseThrow(()-> new BadRequestException(
                        "You must register a business to perform this action"));
    }

    private void validateBuyerOwnership(Rfq rfq, User currentUser){
        if(!rfq.getBuyerUserId().equals(currentUser.getId())){
            throw new ForbiddenException("Only the RFQ buyer can perform this action");
        }
    }

    //Post RFQ
    @Transactional
    public RfqResponse postRfq(CreateRfqRequest request, User currentUser) {

        // Business is optional for buyers
        Long buyerBusinessId = businessRepository
                .findByOwnerId(currentUser.getId())
                .map(Business::getId)
                .orElse(null);   // null = individual buyer, no business yet

        Rfq rfq = Rfq.builder()
                .title(request.title())
                .description(request.description())
                .unit(request.unit())
                .quantity(request.quantity())
                .targetPrice(request.targetPrice())
                .deliveryLocation(request.deliveryLocation())
                .deadline(request.deadline())
                .buyerUserId(currentUser.getId())
                .buyerBusinessId(buyerBusinessId)   // can be null
                .targetSellerBusinessId(request.targetSellerBusinessId())
                .catalogItemId(request.catalogItemId())
                .status(RfqStatus.OPEN)
                .build();

        return RfqResponse.from(rfqRepository.save(rfq));
    }

    //Rfq by id
    @Transactional(readOnly = true)
    public RfqResponse getById(Long id){
        return RfqResponse.from(findByID(id));
    }

    //Poster rfq
    @Transactional(readOnly = true)
    public PagedResponse<RfqResponse> getMyRfqs(User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                rfqRepository.findByBuyerUserId(currentUser.getId(), pageable)
                        .map(RfqResponse::from));
    }

    //Open rfq
    @Transactional(readOnly = true)
    public PagedResponse<RfqResponse> getOpenRfqs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PagedResponse.from(
                rfqRepository.findByStatusAndTargetSellerBusinessIdIsNull(
                                RfqStatus.OPEN, pageable)
                        .map(RfqResponse::from));
    }

    //Cancel rfq
    @Transactional
    public RfqResponse cancelRfq(Long rfqId, User currentUser) {
        Rfq rfq = findByID(rfqId);
        validateBuyerOwnership(rfq, currentUser);

        if (rfq.getStatus() == RfqStatus.ACCEPTED) {
            throw new BadRequestException(
                    "Cannot cancel an RFQ that has already been accepted.");
        }

        rfq.setStatus(RfqStatus.REJECTED);
        return RfqResponse.from(rfqRepository.save(rfq));
    }

    //Submit quote
    @Transactional
    public QuoteResponse submitQuote(Long rfqId, SubmitQuoteRequest request, User currentUser){
        Rfq rfq = findByID(rfqId);
        Business sellerBusiness = getOwnedBusiness(currentUser);

        // Cannot quote on own RFQ
        if(rfq.getBuyerBusinessId()!=null && rfq.getBuyerBusinessId().equals(sellerBusiness.getId())){
            throw new BadRequestException("You cannot submit a quote on your own rfq");
        }

        // RFQ must be open or negotiating
        if (rfq.getStatus() != RfqStatus.OPEN && rfq.getStatus() != RfqStatus.NEGOTIATING) {
            throw new BadRequestException(
                    "This RFQ is not open for quotes. Current status: " + rfq.getStatus());
        }

        // One quote per seller per RFQ
        if (quoteRepository.existsByRfqIdAndSellerBusinessId(rfqId, sellerBusiness.getId())) {
            throw new BadRequestException(
                    "You have already submitted a quote for this RFQ. Update the existing one.");
        }

        Quote quote = Quote.builder()
                .rfq(rfq)
                .sellerUserId(currentUser.getId())
                .sellerBusinessId(sellerBusiness.getId())
                .pricePerUnit(request.pricePerUnit())
                .availableQuantity(request.availableQuantity())
                .note(request.note())
                .validUntil(request.validUntil())
                .status(QuoteStatus.PENDING)
                .build();

        quoteRepository.save(quote);

        // Transition RFQ to RESPONDED
        if (rfq.getStatus() == RfqStatus.OPEN) {
            rfq.setStatus(RfqStatus.RESPONDED);
            rfqRepository.save(rfq);
        }

        return QuoteResponse.from(quote);
    }

    //Accept quote
    @Transactional
    public RfqResponse acceptQuote(Long rfqId, Long quoteId, User currentUser) {
        Rfq rfq = findByID(rfqId);
        validateBuyerOwnership(rfq, currentUser);

        if (rfq.getStatus() != RfqStatus.RESPONDED &&
                rfq.getStatus() != RfqStatus.NEGOTIATING) {
            throw new BadRequestException(
                    "RFQ must be in RESPONDED or NEGOTIATING state to accept a quote.");
        }

        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quote", String.valueOf(quoteId)));

        if (!quote.getRfq().getId().equals(rfqId)) {
            throw new BadRequestException("This quote does not belong to the specified RFQ.");
        }

        if (quote.getStatus() != QuoteStatus.PENDING) {
            throw new BadRequestException(
                    "Only PENDING quotes can be accepted. Current status: " + quote.getStatus());
        }

        // Accept this quote
        quote.setStatus(QuoteStatus.ACCEPTED);
        quoteRepository.save(quote);

        // Reject all other quotes on this RFQ
        quoteRepository.findByRfqIdAndStatus(rfqId, QuoteStatus.PENDING)
                .forEach(q -> {
                    if (!q.getId().equals(quoteId)) {
                        q.setStatus(QuoteStatus.REJECTED);
                        quoteRepository.save(q);
                    }
                });

        // Transition RFQ to ACCEPTED
        rfq.setStatus(RfqStatus.ACCEPTED);
        return RfqResponse.from(rfqRepository.save(rfq));
    }

    //Reject quote
    @Transactional
    public QuoteResponse rejectQuote(Long rfqId, Long quoteId, User currentUser) {
        Rfq rfq = findByID(rfqId);
        validateBuyerOwnership(rfq, currentUser);

        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quote", String.valueOf(quoteId)));

        if (!quote.getRfq().getId().equals(rfqId)) {
            throw new BadRequestException("This quote does not belong to the specified RFQ.");
        }

        quote.setStatus(QuoteStatus.REJECTED);
        quoteRepository.save(quote);

        // If all quotes rejected, move RFQ back to NEGOTIATING
        List<Quote> pendingQuotes = quoteRepository.findByRfqIdAndStatus(
                rfqId, QuoteStatus.PENDING);
        if (pendingQuotes.isEmpty()) {
            rfq.setStatus(RfqStatus.NEGOTIATING);
            rfqRepository.save(rfq);
        }

        return QuoteResponse.from(quote);
    }

    //Get all quotes
    @Transactional(readOnly = true)
    public List<QuoteResponse> getQuotesForRfq(Long rfqId, User currentUser) {
        Rfq rfq = findByID(rfqId);

        // Only buyer of the RFQ can see all quotes
        if (!rfq.getBuyerUserId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the RFQ owner can view all quotes.");
        }

        return quoteRepository.findByRfqId(rfqId)
                .stream()
                .map(QuoteResponse::from)
                .toList();
    }
}
