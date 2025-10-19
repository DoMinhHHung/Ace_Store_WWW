package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.CreateProductRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.ProductService;
import org.springframework.data.domain.Pageable;

public class ProductServiceImpl implements ProductService {
    @Override
    public ApiResponse createProduct(CreateProductRequest request) {
        return null;
    }

    @Override
    public ApiResponse getProductDetail(Long productId) {
        return null;
    }

    @Override
    public ApiResponse getAllProducts(Pageable pageable) {
        return null;
    }

    @Override
    public ApiResponse updateProduct(Long productId, CreateProductRequest request) {
        return null;
    }

    @Override
    public ApiResponse deleteProduct(Long productId) {
        return null;
    }
}
