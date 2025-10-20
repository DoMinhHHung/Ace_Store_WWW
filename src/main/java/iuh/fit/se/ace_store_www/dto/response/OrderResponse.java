package iuh.fit.se.ace_store_www.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String status;
    private BigDecimal amount;
    private String message;
}