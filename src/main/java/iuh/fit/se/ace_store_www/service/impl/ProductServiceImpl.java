package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.CreateProductRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdateProductRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.ProductResponse;
import iuh.fit.se.ace_store_www.dto.response.PromotionResponse;
import iuh.fit.se.ace_store_www.entity.*;
import iuh.fit.se.ace_store_www.repository.*; // Import tất cả Repositories
import iuh.fit.se.ace_store_www.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductSpecificationRepository productSpecificationRepository;

    private ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setMainImageUrl(product.getMainImage());
        response.setProductType(product.getProductType());

        if (product.getPromotion() != null) {
            Promotion p = product.getPromotion();
            response.setPromotion(new PromotionResponse(
                    p.getId(), p.getName(), p.getDescription(), p.getDiscountPercent(),
                    p.getStartDate(), p.getEndDate(), p.isActive()
            ));
        }

        response.setDetailImageUrls(product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));

        response.setSpecifications(product.getSpecifications().stream()
                .collect(Collectors.toMap(
                        ProductSpecification::getSpecName,
                        ProductSpecification::getSpecValue,
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new
                )));

        return response;
    }

    @Override
    @Transactional
    public ApiResponse createProduct(CreateProductRequest request) {
        if (productRepository.findByName(request.getName()) != null) {
            return ApiResponse.fail("Product name already exists, bro.");
        }

        Promotion promotion = null;
        if (request.getPromotionId() != null) {
            promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + request.getPromotionId()));
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setMainImage(request.getMainImagePublicId());
        product.setProductType(request.getProductType());
        product.setPromotion(promotion);

        Product savedProduct = productRepository.save(product);

        Set<ProductImage> detailImages = request.getDetailImagePublicIds().stream()
                .map(publicId -> new ProductImage(null, publicId, publicId, savedProduct)) // Tạm dùng publicId cho imageUrl
                .collect(Collectors.toSet());
        productImageRepository.saveAll(detailImages);
        savedProduct.setProductImages(detailImages);

        Set<ProductSpecification> specs = request.getSpecifications().entrySet().stream()
                .map(entry -> new ProductSpecification(null, entry.getKey(), entry.getValue(), savedProduct))
                .collect(Collectors.toSet());
        productSpecificationRepository.saveAll(specs);
        savedProduct.setSpecifications(specs);

        return ApiResponse.ok("Product created successfully.", toProductResponse(savedProduct));
    }

    @Override
    public ApiResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        return ApiResponse.ok(toProductResponse(product));
    }

    @Override
    public ApiResponse getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        Map<String, Object> pageData = Map.of(
                "products", productPage.getContent().stream().map(this::toProductResponse).collect(Collectors.toList()),
                "totalPages", productPage.getTotalPages(),
                "totalElements", productPage.getTotalElements(),
                "currentPage", productPage.getNumber()
        );

        return ApiResponse.ok("Products retrieved successfully.", pageData);
    }

    @Override
    @Transactional
    public ApiResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setMainImage(request.getMainImagePublicId());
        existingProduct.setProductType(request.getProductType());

        Promotion promotion = null;
        if (request.getPromotionId() != null) {
            promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + request.getPromotionId()));
        }
        existingProduct.setPromotion(promotion);
        existingProduct.getProductImages().clear();
        Set<ProductImage> newImages = request.getDetailImagePublicIds().stream()
                .map(publicId -> new ProductImage(null, publicId, publicId, existingProduct))
                .collect(Collectors.toSet());
        productImageRepository.saveAll(newImages);

        existingProduct.getSpecifications().clear();
        Set<ProductSpecification> newSpecs = request.getSpecifications().entrySet().stream()
                .map(entry -> new ProductSpecification(null, entry.getKey(), entry.getValue(), existingProduct))
                .collect(Collectors.toSet());
        productSpecificationRepository.saveAll(newSpecs);

        Product updatedProduct = productRepository.save(existingProduct);
        return ApiResponse.ok("Product updated successfully.", toProductResponse(updatedProduct));
    }

    @Override
    public ApiResponse deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            return ApiResponse.fail("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
        return ApiResponse.ok("Product deleted successfully.", null);
    }
}