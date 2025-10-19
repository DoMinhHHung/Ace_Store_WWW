package iuh.fit.se.ace_store_www.service;

import iuh.fit.se.ace_store_www.dto.request.CreatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    // CRUD Operations
    ApiResponse createPromotion(CreatePromotionRequest request);
    ApiResponse getPromotionDetail(Long promotionId);
    ApiResponse getAllPromotions(Pageable pageable);
    ApiResponse updatePromotion(Long promotionId, CreatePromotionRequest request);
    ApiResponse deletePromotion(Long promotionId);

    // Business Logic
    ApiResponse togglePromotionActiveStatus(Long promotionId, boolean isActive);
}