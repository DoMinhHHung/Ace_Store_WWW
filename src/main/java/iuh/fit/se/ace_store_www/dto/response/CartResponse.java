package iuh.fit.se.ace_store_www.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private String sessionId;
    private List<CartItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal total;
    private String currency;
}