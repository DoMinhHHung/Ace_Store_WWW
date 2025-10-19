package iuh.fit.se.ace_store_www.dto.response;

import iuh.fit.se.ace_store_www.entity.enums.ProductType;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String mainImageUrl;
    private ProductType productType;
    private PromotionResponse promotion;
    private List<String> detailImageUrls;
    private Map<String, String> specifications;
}