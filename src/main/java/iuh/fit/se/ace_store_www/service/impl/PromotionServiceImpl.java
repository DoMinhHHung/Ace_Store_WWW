package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.CreatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdatePromotionRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.PromotionResponse;
import iuh.fit.se.ace_store_www.entity.Product;
import iuh.fit.se.ace_store_www.entity.Promotion;
import iuh.fit.se.ace_store_www.repository.ProductRepository;
import iuh.fit.se.ace_store_www.repository.PromotionRepository;
import iuh.fit.se.ace_store_www.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    private PromotionResponse toPromotionResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setName(promotion.getName());
        response.setDescription(promotion.getDescription());
        response.setDiscountPercent(promotion.getDiscountPercent());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        response.setActive(promotion.isActive());
        return response;
    }

    @Override
    public ApiResponse createPromotion(CreatePromotionRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            return ApiResponse.error("End Date must be after Start Date, bro.");
        }
        if (promotionRepository.findByName(request.getName()) != null) {
            return ApiResponse.error("Promotion name already exists.");
        }

        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountPercent(request.getDiscountPercent());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        boolean isActive = LocalDateTime.now().isBefore(request.getEndDate()) &&
                !LocalDateTime.now().isBefore(request.getStartDate());
        promotion.setActive(isActive);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return ApiResponse.success("Promotion created successfully.", toPromotionResponse(savedPromotion));
    }

    @Override
    public ApiResponse getPromotionDetail(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));
        return ApiResponse.success(toPromotionResponse(promotion));
    }

    @Override
    public ApiResponse getAllPromotions(Pageable pageable) {
        Page<Promotion> promotionPage = promotionRepository.findAll(pageable);

        Map<String, Object> pageData = Map.of(
                "promotions", promotionPage.getContent().stream().map(this::toPromotionResponse).collect(Collectors.toList()),
                "totalPages", promotionPage.getTotalPages(),
                "totalElements", promotionPage.getTotalElements(),
                "currentPage", promotionPage.getNumber()
        );

        return ApiResponse.success("Promotions retrieved successfully.", pageData);
    }

    @Override
    public ApiResponse updatePromotion(Long promotionId, UpdatePromotionRequest request) {
        Promotion existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            return ApiResponse.error("End Date must be after Start Date, bro.");
        }
        Promotion promotionByName = promotionRepository.findByName(request.getName());
        if (promotionByName != null && !promotionByName.getId().equals(promotionId)) {
            return ApiResponse.error("Promotion name already exists.");
        }

        existingPromotion.setName(request.getName());
        existingPromotion.setDescription(request.getDescription());
        existingPromotion.setDiscountPercent(request.getDiscountPercent());
        existingPromotion.setStartDate(request.getStartDate());
        existingPromotion.setEndDate(request.getEndDate());

        boolean isActive = LocalDateTime.now().isBefore(request.getEndDate()) &&
                !LocalDateTime.now().isBefore(request.getStartDate());
        existingPromotion.setActive(isActive);

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return ApiResponse.success("Promotion updated successfully.", toPromotionResponse(updatedPromotion));
    }

    @Override
    @Transactional
    public ApiResponse deletePromotion(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));
        for (Product product : promotion.getProducts()) {
            product.setPromotion(null);
        }
        productRepository.saveAll(promotion.getProducts());

        promotionRepository.deleteById(promotionId);
        return ApiResponse.success("Promotion deleted successfully.", null);
    }

    @Override
    public ApiResponse togglePromotionActiveStatus(Long promotionId, boolean isActive) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        promotion.setActive(isActive);
        Promotion updatedPromotion = promotionRepository.save(promotion);

        return ApiResponse.success("Promotion active status updated.", toPromotionResponse(updatedPromotion));
    }
}