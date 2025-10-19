package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.CreateProductRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdateProductRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.ProductResponse;
import iuh.fit.se.ace_store_www.dto.response.PromotionResponse;
import iuh.fit.se.ace_store_www.entity.*;
import iuh.fit.se.ace_store_www.repository.*;
import iuh.fit.se.ace_store_www.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
    public ApiResponse updateProduct(Long productId, UpdateProductRequest request) {
        return null;
    }

    @Override
    public ApiResponse deleteProduct(Long productId) {
        return null;
    }
}