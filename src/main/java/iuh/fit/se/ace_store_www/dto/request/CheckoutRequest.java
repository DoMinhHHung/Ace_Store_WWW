package iuh.fit.se.ace_store_www.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    private String paymentMethod;
    private Long shippingAddressId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String couponCode;
}