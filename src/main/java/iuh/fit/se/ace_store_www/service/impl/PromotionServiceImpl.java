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
import java.time.LocalTime;
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
        // stored in entity as fraction (0.0..1.0) -> return percent (0..100)
        response.setDiscountPercent(promotion.getDiscountPercent() != null ? promotion.getDiscountPercent() * 100 : null);
        // convert LocalDateTime -> LocalDate for response (null-safe)
        response.setStartDate(promotion.getStartDate() );
        response.setEndDate(promotion.getEndDate());
        response.setActive(promotion.isActive());
        return response;
    }

    @Override
    public ApiResponse createPromotion(CreatePromotionRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            return ApiResponse.fail("End Date must be after Start Date, bro.");
        }
        if (promotionRepository.findByName(request.getName()) != null) {
            return ApiResponse.fail("Promotion name already exists.");
        }

        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        // request.discountPercent is percent (0..100) -> store as fraction (0.0..1.0)
        promotion.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() / 100.0 : null);
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        boolean isActive = LocalDateTime.now().isBefore(promotion.getEndDate()) &&
                !LocalDateTime.now().isBefore(promotion.getStartDate());
        promotion.setActive(isActive);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return ApiResponse.ok("Promotion created successfully.", toPromotionResponse(savedPromotion));
    }

    @Override
    public ApiResponse getPromotionDetail(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));
        return ApiResponse.ok(toPromotionResponse(promotion));
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

        return ApiResponse.ok("Promotions retrieved successfully.", pageData);
    }

    @Override
    public ApiResponse updatePromotion(Long promotionId, UpdatePromotionRequest request) {
        Promotion existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        if (request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            return ApiResponse.fail("End Date must be after Start Date");
        }
        Promotion promotionByName = promotionRepository.findByName(request.getName());
        if (promotionByName != null && !promotionByName.getId().equals(promotionId)) {
            return ApiResponse.fail("Promotion name already exists.");
        }

        if (request.getName() != null) existingPromotion.setName(request.getName());
        if (request.getDescription() != null) existingPromotion.setDescription(request.getDescription());
        if (request.getDiscountPercent() != null) existingPromotion.setDiscountPercent(request.getDiscountPercent() / 100.0);
        if (request.getStartDate() != null) {
            existingPromotion.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            existingPromotion.setEndDate(request.getEndDate());
        }

        boolean isActive = LocalDateTime.now().isBefore(existingPromotion.getEndDate()) &&
                !LocalDateTime.now().isBefore(existingPromotion.getStartDate());
        existingPromotion.setActive(isActive);

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return ApiResponse.ok("Promotion updated successfully.", toPromotionResponse(updatedPromotion));
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
        return ApiResponse.ok("Promotion deleted successfully.", null);
    }

    @Override
    public ApiResponse togglePromotionActiveStatus(Long promotionId, boolean isActive) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        promotion.setActive(isActive);
        Promotion updatedPromotion = promotionRepository.save(promotion);

        return ApiResponse.ok("Promotion active status updated.", toPromotionResponse(updatedPromotion));
    }
}