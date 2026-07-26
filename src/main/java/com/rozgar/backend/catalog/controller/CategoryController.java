package com.rozgar.backend.catalog.controller;


import com.rozgar.backend.catalog.dto.response.CategoryResponse;
import com.rozgar.backend.catalog.entity.Category;
import com.rozgar.backend.catalog.repository.CategoryRepository;
import com.rozgar.backend.common.exception.ConflictException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(){
        List<CategoryResponse> categories = categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestParam String name,
            @RequestParam(required = false) String description){

        if(categoryRepository.existsByNameIgnoreCase(name)){
            throw new ConflictException("Category already exists:" + name);
        }

        Category saved = categoryRepository.save(Category.builder()
                .name(name)
                .description(description)
                .build());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", CategoryResponse.from(saved)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category", String.valueOf(id)));
        categoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}
