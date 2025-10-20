package iuh.fit.se.ace_store_www.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal lineTotal;
    private String imageUrl;
    private Long promotionId;
}