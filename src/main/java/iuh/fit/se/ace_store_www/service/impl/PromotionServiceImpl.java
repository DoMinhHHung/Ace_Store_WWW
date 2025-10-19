package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.CreatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.PromotionService;
import org.springframework.data.domain.Pageable;

public class PromotionServiceImpl implements PromotionService {
    @Override
    public ApiResponse createPromotion(CreatePromotionRequest request) {
        return null;
    }

    @Override
    public ApiResponse getPromotionDetail(Long promotionId) {
        return null;
    }

    @Override
    public ApiResponse getAllPromotions(Pageable pageable) {
        return null;
    }

    @Override
    public ApiResponse updatePromotion(Long promotionId, CreatePromotionRequest request) {
        return null;
    }

    @Override
    public ApiResponse deletePromotion(Long promotionId) {
        return null;
    }

    @Override
    public ApiResponse togglePromotionActiveStatus(Long promotionId, boolean isActive) {
        return null;
    }
}
