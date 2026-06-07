package com.rozgar.backend.catalog.controller;


import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.catalog.dto.request.CreateCatalogItemRequest;
import com.rozgar.backend.catalog.dto.request.UpdateCatalogItemRequest;
import com.rozgar.backend.catalog.dto.response.CatalogItemResponse;
import com.rozgar.backend.catalog.enums.CatalogItemType;
import com.rozgar.backend.catalog.service.CatalogService;
import com.rozgar.backend.common.response.ApiResponse;
import com.rozgar.backend.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @PostMapping
    public ResponseEntity<ApiResponse<CatalogItemResponse>> addItem(
            @RequestBody @Valid CreateCatalogItemRequest request,
            @AuthenticationPrincipal User currentUser){

        CatalogItemResponse response = catalogService.addItem(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catalog item added successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogItemResponse>> getById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(catalogService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogItemResponse>> updateItem(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCatalogItemRequest request,
            @AuthenticationPrincipal User currentUser){

        CatalogItemResponse response = catalogService.updateItem(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        catalogService.deleteItem(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PagedResponse<CatalogItemResponse>>> getMyCatalog(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(catalogService.getMyCatalog(currentUser, page, size)));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<PagedResponse<CatalogItemResponse>>> getBusinessCatalog(
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        return ResponseEntity.ok(
                ApiResponse.success(
                        catalogService.getBusinessCatalog(businessId, page, size)));
    }

    @GetMapping("/browse")
    public ResponseEntity<ApiResponse<PagedResponse<CatalogItemResponse>>> browse(
            @RequestParam(required = false)CatalogItemType type,
            @RequestParam(required = false)Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        if(categoryId!=null){
            return ResponseEntity.ok(
                    ApiResponse.success(
                            catalogService.browseByCategory(categoryId, page, size)));
        }

        if(type!=null){
            return ResponseEntity.ok(
                    ApiResponse.success(
                            catalogService.browseByType(type, page, size)));
        }

        return ResponseEntity.ok(
                ApiResponse.success("Provide type or categoryId to browse", null));

    }
}
