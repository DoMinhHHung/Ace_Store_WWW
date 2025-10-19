package iuh.fit.se.ace_store_www.service;

import iuh.fit.se.ace_store_www.dto.request.CreateProductRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdateProductRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ApiResponse createProduct(CreateProductRequest request);
    ApiResponse getProductDetail(Long productId);
    ApiResponse getAllProducts(Pageable pageable);
    ApiResponse updateProduct(Long productId, UpdateProductRequest request);
    ApiResponse deleteProduct(Long productId);
}