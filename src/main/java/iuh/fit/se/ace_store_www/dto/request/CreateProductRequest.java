package iuh.fit.se.ace_store_www.dto.request;

import iuh.fit.se.ace_store_www.entity.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Data
public class CreateProductRequest {
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Integer stock;

    private String mainImagePublicId;
    private Set<String> detailImagePublicIds;

    private ProductType productType;

    private Long promotionId;

    private Map<String, String> specifications;
}