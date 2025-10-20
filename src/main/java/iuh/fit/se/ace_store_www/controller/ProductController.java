package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.dto.request.CreateProductRequest;
import iuh.fit.se.ace_store_www.dto.request.UpdateProductRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ApiResponse response = productService.createProduct(request);
        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long id) {
        try {
            ApiResponse response = productService.getProductDetail(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts(Pageable pageable) {
        ApiResponse response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateProductRequest request) {
        try {
            ApiResponse response = productService.updateProduct(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    // DELETE: Xóa Sản phẩm
    // URL: /api/v1/products/123
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        ApiResponse response = productService.deleteProduct(id);
        return response.isSuccess()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body(response);
    }
}