package com.rozgar.backend.business.controller;


import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.dto.request.CreateBusinessRequest;
import com.rozgar.backend.business.dto.request.UpdateBusinessRequest;
import com.rozgar.backend.business.dto.response.BusinessResponse;
import com.rozgar.backend.business.enums.BusinessType;
import com.rozgar.backend.business.service.BusinessService;
import com.rozgar.backend.common.response.ApiResponse;
import com.rozgar.backend.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping //Create new business
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BusinessResponse>> create(
            @RequestBody @Valid CreateBusinessRequest request,
            @AuthenticationPrincipal User currentUser){

        BusinessResponse response = businessService.create(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Business registered successfully", response));
    }

    @GetMapping("/me") //Get user business
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BusinessResponse>> getMyBusiness(
            @AuthenticationPrincipal User currentUser){

        BusinessResponse response = businessService.getMyBusiness(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")  //Get any business
    public ResponseEntity<ApiResponse<BusinessResponse>> getById(@PathVariable Long id) {
        BusinessResponse response = businessService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")  //Update your business
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BusinessResponse>> update (
            @PathVariable Long id, @RequestBody @Valid UpdateBusinessRequest request,
            @AuthenticationPrincipal User currentUser){

        BusinessResponse response = businessService.update(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Business updated successfully", response));
    }

    @DeleteMapping("/{id}") //Delete your business
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser){

        businessService.delete(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Business deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BusinessResponse>>> browse(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BusinessType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){


        PagedResponse<BusinessResponse> response =
                businessService.browse(city, type, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
