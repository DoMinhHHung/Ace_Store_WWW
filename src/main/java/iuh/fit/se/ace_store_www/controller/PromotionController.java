package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.dto.request.CreatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.PromotionService; // Inject Interface Service
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ApiResponse> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        ApiResponse response = promotionService.createPromotion(request);
        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPromotion(@PathVariable Long id) {
        try {
            ApiResponse response = promotionService.getPromotionDetail(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPromotions(Pageable pageable) {
        ApiResponse response = promotionService.getAllPromotions(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePromotion(@PathVariable Long id,
                                                       @Valid @RequestBody UpdatePromotionRequest request) {
        try {
            ApiResponse response = promotionService.updatePromotion(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse> toggleActive(@PathVariable Long id, @RequestParam boolean isActive) {
        ApiResponse response = promotionService.togglePromotionActiveStatus(id, isActive);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePromotion(@PathVariable Long id) {
        ApiResponse response = promotionService.deletePromotion(id);
        return response.isSuccess()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body(response);
    }
}