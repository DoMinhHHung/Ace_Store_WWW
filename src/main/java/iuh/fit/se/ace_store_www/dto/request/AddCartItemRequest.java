package iuh.fit.se.ace_store_www.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartItemRequest {
    private Long productId;
    private Integer quantity;
}